package com.book.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("product_images")
public class ProductImage implements Comparable<ProductImage> {

    @Id
    @Column("image_id")
    private Long imageId;

    @Column("image_url")
    private String imageUrl;

    @Column("sort_order")
    private Integer sortOrder;

    @Override
    public int compareTo(ProductImage other) {
        return java.util.Comparator
                .comparing(ProductImage::getSortOrder,
                        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()))
                .compare(this, other);
    }
}
