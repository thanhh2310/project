package com.example.project.Service;

import com.example.project.Config.WebErrorConfig;
import com.example.project.DTO.Request.AddAttributesToCategoryRequest;
import com.example.project.DTO.Request.CategoryAttributeRequest;
import com.example.project.DTO.Request.CategoryCreationRequest;
import com.example.project.DTO.Request.UpdateCategoryRequest;
import com.example.project.DTO.Response.CategoryAttributeResponse;
import com.example.project.DTO.Response.CategoryResponse;
import com.example.project.Enum.ErrorCode;
import com.example.project.Mapper.CategoryMapper;
import com.example.project.Model.Attribute;
import com.example.project.Model.Category;
import com.example.project.Model.CategoryAttribute;
import com.example.project.Repository.AttributeRepository;
import com.example.project.Repository.CategoryAttributeRepository;
import com.example.project.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AttributeRepository attributeRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;
    //---- for admin
    @Transactional
    public CategoryResponse createCategory(CategoryCreationRequest request){
        // tao 1 thang CategoryMoi
        Category category = categoryMapper.requestToCategory(request);

        // --- XỬ LÝ SLUG
        if((request.getSlug() != null) &&(!request.getSlug().isEmpty()) ){
            category.setSlug(request.getSlug());
        } else {
            category.setSlug(generateSlug(request.getName()));
        }

        // --- XỬ LÝ PARENT (CHA) ---
        // neu co no co cha thi nhet vao builder
        if(request.getParentId() != null){
            // tim thang cha bang id
            Category categoryParent = (Category) categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new WebErrorConfig(ErrorCode.CATEGORY_NOT_FOUND));
            // nhet thang cha vao builder
            category.setParent(categoryParent);
        }else category.setParent(null);

        // check xem da co slug chua
        if(categoryRepository.existsBySlug(category.getSlug())){
            throw new WebErrorConfig(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        // luu thang nay vao db
        categoryRepository.save(category);
        return categoryMapper.categoryToResponse(category, true);
    }

    @Transactional
    public CategoryResponse updateCategory(UpdateCategoryRequest request, Integer id){
        // tim thang can update bang Id
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new WebErrorConfig(ErrorCode.CATEGORY_NOT_FOUND));

        //map du lieu thang can update
        categoryMapper.updateToCategory(category, request);

        //Xu ly slug
        if(request.getSlug() != null && !request.getSlug().isEmpty()){
            category.setSlug(request.getSlug());
        } else if (request.getName() != null) {
            category.setSlug(generateSlug(request.getName()));
        }

        //Xu ly du lieu parent
        Integer newParentId = request.getParentId();
        if(newParentId != null){
            //neu muon bo thang cha de thanh la 1 thang cha moi
            if (newParentId == 0){
                category.setParent(null);
            }
            else{
                if(Objects.equals(request.getParentId(), id)){
                    throw new WebErrorConfig(ErrorCode.INVALID_PARENT);
                }
                Category categoryParent = (Category) categoryRepository.findById(newParentId)
                        .orElseThrow(() -> new WebErrorConfig(ErrorCode.CATEGORY_NOT_FOUND));
                category.setParent(categoryParent);
            }
        }

        // check xem da co slug chua
        if(categoryRepository.existsBySlugAndIdNot(category.getSlug(), id)){
            throw new WebErrorConfig(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
        // luu thang nay vao db
        categoryRepository.save(category);
        return categoryMapper.categoryToResponse(category, true);

    }

    public CategoryResponse getCategoryById(Integer id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new WebErrorConfig(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.categoryToResponse(category, true);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategory(){
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> categoryMapper.categoryToResponse(category, true))
                .collect(Collectors.toList());
    }

    public void deleteCategory(Integer id){
        if (!categoryRepository.existsById(id)) {
            throw new WebErrorConfig(ErrorCode.CATEGORY_NOT_FOUND);
        }
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void addAttributeToCategory(Integer categoryId, AddAttributesToCategoryRequest request){
        // 1. Tìm Category (Cha)
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new WebErrorConfig(ErrorCode.CATEGORY_NOT_FOUND));

        // a. Lấy danh sách ID từ request gửi lên
        List<Integer> attributeIds = request.getAttributes().stream()
                .map(CategoryAttributeRequest::getAttributeId)
                .collect(Collectors.toList());
        // b. Query DB 1 lần duy nhất để lấy tất cả Attribute đó
        List<Attribute> attributes = attributeRepository.findAllById(attributeIds);

        // c. Chuyển List thành Map<ID, Attribute> để tí nữa tìm kiếm cho nhanh (O(1))
        // Nếu không làm bước này, tí nữa phải chạy vòng lặp lồng nhau -> chậm
        Map<Integer , Attribute> attributeMap = attributes.stream()
                .collect(Collectors.toMap(Attribute::getId, Function.identity()));

        // lay ra cac cap lien ket giua cac category va attribute da ton tai
        List<CategoryAttribute> existingLinks = categoryAttributeRepository.findByCategoryId(categoryId);

        // chuyen List thanh Map<Id, CategoryAttribute> de ty tim kiem cho nhanh
        Map<Integer, CategoryAttribute> existingLinkMap = existingLinks.stream()
                .collect(Collectors.toMap(
                        link -> link.getAttribute().getId(),
                        Function.identity()
                ));

        List<CategoryAttribute> toSaveList = new ArrayList<>();
        for(CategoryAttributeRequest reqItem : request.getAttributes()){
            Attribute attribute = attributeMap.get(reqItem.getAttributeId());
            if (attribute==null) throw new WebErrorConfig(ErrorCode.ATTRIBUTE_NOT_FOUND);
            if(existingLinkMap.containsKey(attribute.getId())){
                CategoryAttribute existingLink = existingLinkMap.get(attribute.getId());
                existingLink.setIsRequired(reqItem.getIsRequired());
                existingLink.setIsFilterable(reqItem.getIsFilterable());

                toSaveList.add(existingLink);
            }else {
                CategoryAttribute newLink = CategoryAttribute.builder()
                        .category(category)
                        .attribute(attribute)
                        .isRequired(reqItem.getIsRequired())
                        .isFilterable(reqItem.getIsFilterable())
                        .build();
                toSaveList.add(newLink);
            }
        }

        // 3. Lưu tất cả vào bảng category_attributes (Batch Insert)
        categoryAttributeRepository.saveAll(toSaveList);

        toSaveList.forEach(item ->
                System.out.println("ID=" + item.getId()
                        + " | createdAt=" + item.getCreatedAt())
        );

    }

    // Api get data
    @Transactional(readOnly = true)
    public List<CategoryAttributeResponse> getAttributesByCategory(Integer categoryId){
        List<CategoryAttribute> links = categoryAttributeRepository.findByCategoryId(categoryId);
        return links.stream().map(link -> CategoryAttributeResponse.builder()
                        .attributeId(link.getAttribute().getId())
                        .attributeName(link.getAttribute().getName()) // Lấy tên từ bảng Attribute
                        .isRequired(link.getIsRequired())
                        .isFilterable(link.getIsFilterable())
                        .build())
                .collect(Collectors.toList());
    }

    private String generateSlug(String name) {
        if (name == null) return null;

        // 1. Chuyển thành chữ thường
        String temp = name.toLowerCase();

        // 2. Chuẩn hóa Unicode để tách dấu ra khỏi chữ (Ví dụ: ấ -> a + dấu sắc)
        temp = Normalizer.normalize(temp, Normalizer.Form.NFD);

        // 3. Dùng Regex để loại bỏ các dấu
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");

        // 4. Thay thế chữ Đ/đ thành d (Vì Normalizer không xử lý chữ Đ)
        temp = temp.replace("đ", "d");

        // 5. Thay khoảng trắng và các ký tự đặc biệt thành dấu gạch ngang
        temp = temp.replaceAll("[^a-z0-9\\s-]", ""); // Bỏ ký tự lạ
        temp = temp.replaceAll("\\s+", "-"); // Khoảng trắng thành -

        return temp;
    }
}
