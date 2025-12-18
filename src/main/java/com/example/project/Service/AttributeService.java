package com.example.project.Service;

import com.example.project.Config.WebErrorConfig;
import com.example.project.DTO.Request.AttributeCreationRequest;
import com.example.project.DTO.Request.AttributeValueUpdateRequest;
import com.example.project.DTO.Request.UpdateAttributeRequest;
import com.example.project.DTO.Response.AttributeResponse;
import com.example.project.Enum.ErrorCode;
import com.example.project.Mapper.AttributeMapper;
import com.example.project.Model.Attribute;
import com.example.project.Model.AttributeValue;
import com.example.project.Repository.AttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttributeService {
    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;

    @Transactional(readOnly = true)
    public List<AttributeResponse> getAllAttribute(){
        List<Attribute> attribute = attributeRepository.findAll();
        return attribute.stream()
                .map(attributeMapper::attributeToResponse)
                .collect(Collectors.toList());
    }

    public AttributeResponse getById(Integer id){
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(()-> new WebErrorConfig(ErrorCode.ATTRIBUTE_NOT_FOUND));
        return attributeMapper.attributeToResponse(attribute);
    }

    @Transactional
    public AttributeResponse createAttribute(AttributeCreationRequest request){
        if(attributeRepository.existsByName(request.getName())){
            throw new WebErrorConfig(ErrorCode.ATTRIBUTE_ALREADY_EXISTS);
        }

        // BƯỚC 1: Lưu Cha trước -> Lấy ID chắc chắn
        Attribute attribute = attributeMapper.requestToAttribute(request);
        attribute.setIsActive(true);
        // Xóa dòng setValues nếu mapper lỡ map values vào
        attribute.setValues(null);

        // Save lần 1: Để DB sinh ra ID cho thằng Cha
        Attribute savedAttribute = attributeRepository.save(attribute);

        // BƯỚC 2: Tạo con và gắn ID cha vừa sinh ra vào
        if(request.getValues() != null && !request.getValues().isEmpty()){
            List<AttributeValue> values = request.getValues().stream()
                    .map(val -> AttributeValue.builder()
                            .value(val)
                            .attribute(savedAttribute) // 👉 Chắc chắn ID không null
                            .build())
                    .collect(Collectors.toList());

            savedAttribute.setValues(values);
            // Save lần 2: Để Hibernate lưu đám con
            attributeRepository.save(savedAttribute);
        }

        return attributeMapper.attributeToResponse(savedAttribute);
    }

    @Transactional
    public AttributeResponse updateAttribute(UpdateAttributeRequest request, Integer id){
        // tim thang can update
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(()-> new WebErrorConfig(ErrorCode.ATTRIBUTE_NOT_FOUND));

        //update cac thuoc tinh co ban
        // Validate trùng tên (Nếu đổi tên thì phải check xem tên mới có trùng ai không)
        if (request.getName() != null && !request.getName().equals(attribute.getName())) {
            if(attributeRepository.existsByName(request.getName())){
                throw new WebErrorConfig(ErrorCode.ATTRIBUTE_ALREADY_EXISTS);
            }
            attribute.setName(request.getName());
        }
        if(request.getDescription() != null) attribute.setDescription(request.getDescription());

        // xu ly values
        if(request.getValues() != null && !request.getValues().isEmpty()){
            // thang attribute dang co 1 list cac value
            // lay list value do ra
            List<AttributeValue> currentValues = attribute.getValues();

            // Biến List hiện tại thành Map<ID, AttributeValue>
            Map<Integer, AttributeValue> currentMap = currentValues.stream()
                    .collect(Collectors.toMap(AttributeValue::getId, val -> val));

            // Dùng Set để lưu lại những ID có trong Request (để tí nữa tính toán việc xóa)
            Set<Integer> requestIds = new HashSet<>();

            //duyet qua tung yeu cau thay doi ma nguoi dung gui
            for (AttributeValueUpdateRequest valReq : request.getValues()){
                //neu id khac null thi yeu cua thay doi attribute-value bang 1 attribute-valu da ton tai
                if(valReq.getId() != null){
                    AttributeValue existing = currentMap.get(valReq.getId());
                    if (existing != null) {
                        existing.setValue(valReq.getValue());
                        requestIds.add(valReq.getId()); // Đánh dấu là "được giữ lại"
                    }
                }else {
                    //neu id = null thi them 1 attribute-value moi
                    AttributeValue newVal = AttributeValue.builder()
                            .value(valReq.getValue())
                            .attribute(attribute)
                            .build();
                    currentValues.add(newVal);
                }
            }
//            currentValues.removeIf(val ->
//                    val.getId() != null && !requestIds.contains(val.getId())
//            );
        }


        attributeRepository.save(attribute);

        return attributeMapper.attributeToResponse(attribute);
    }

    public void deleteAttribute(Integer id){
        if (!attributeRepository.existsById(id)) {
            throw new WebErrorConfig(ErrorCode.ATTRIBUTE_NOT_FOUND);
        }
        attributeRepository.deleteById(id);
    }
}
