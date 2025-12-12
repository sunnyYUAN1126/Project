package com.book.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return "ProductImage{" +
                "imageId=" + imageId +
                ", imageUrl='" + imageUrl + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
