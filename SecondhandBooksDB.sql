-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: secondhand_books_db
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `secondhand_books_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `secondhand_books_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `secondhand_books_db`;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carts` (
  `cart_id` bigint NOT NULL AUTO_INCREMENT,
  `buyer_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cart_id`),
  KEY `fk_cart_buyer` (`buyer_id`),
  KEY `fk_cart_product` (`product_id`),
  CONSTRAINT `fk_cart_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `orders_id` bigint NOT NULL AUTO_INCREMENT,
  `buyer_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `meetup_location` enum('管院前門','文學院前門','理學院前門','醫學院前門') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `meetup_date` date NOT NULL,
  `meetup_time` enum('第一節下課','第二節下課','第三節下課','第四節下課','第五節下課','第六節下課','第七節下課','第八節下課') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('取消','待面交','完成') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '待面交',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`orders_id`),
  KEY `fk_orders_buyer` (`buyer_id`),
  KEY `fk_orders_seller` (`seller_id`),
  KEY `fk_orders_product` (`product_id`),
  CONSTRAINT `fk_orders_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_orders_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_orders_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_images`
--

DROP TABLE IF EXISTS `product_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_images` (
  `image_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `fk_images_product` (`product_id`),
  CONSTRAINT `fk_images_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_images`
--

LOCK TABLES `product_images` WRITE;
/*!40000 ALTER TABLE `product_images` DISABLE KEYS */;
INSERT INTO `product_images` VALUES (1,1,'D:\\Project\\picture\\1-1.jpg',1),(2,1,'D:\\Project\\picture\\1-2.jpg',2),(3,1,'D:\\Project\\picture\\1-3.jpg',3),(7,7,'D:\\Project\\picture\\9789865030612.jpg',1);
/*!40000 ALTER TABLE `product_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `seller_id` bigint NOT NULL,
  `book_ISBN` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `book_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` enum('文學類','社會科學類','商業管理類','理工資訊類','醫學健康類') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `book_author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `book_publisher` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_new` enum('九成','八成','七成','六成','五成','四成','三成','二成','一成') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_class_note` enum('無','少量筆記','大量筆記') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `product_price` int NOT NULL,
  `product_stock` int DEFAULT '1',
  `shelf_status` enum('上架','下架') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '下架',
  `admin_review` enum('待審核','審核通過','審核不通過') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '待審核',
  `admin_note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`),
  KEY `fk_products_seller` (`seller_id`),
  CONSTRAINT `fk_products_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,2,'9789865030612','計算機概論: 當代資訊通鑑 (第14版)','商業管理類','趙坤茂/ 張雅惠/ 黃俊穎/ 黃寶萱','全華圖書股份有限公司','九成','無','書頁泛黃',50,1,'上架','審核通過',NULL,'2025-12-05 16:09:41','2025-12-07 11:54:52'),(2,2,'9789869462686','運籌視野之供應鏈管理 (10版)','商業管理類','John J. Coyle, C. John Langley, Jr., Robert A. Novack, Brian J. Gibson','東華','九成','無','',100,0,'下架','審核通過',NULL,'2025-12-04 16:09:41','2025-12-04 16:09:41'),(3,2,'9789865774998','資料庫的核心理論與實務 (7版)','商業管理類','黃三益','前程文化事業股份有限公司','九成','無','',100,1,'上架','審核通過',NULL,'2025-12-04 16:09:41','2025-12-04 16:09:41'),(4,2,'9789867433633','經濟學概論(三版)','商業管理類','胡春田/巫和懋/霍德明/熊秉元','雙葉書廊','九成','無','',100,1,'上架','審核通過',NULL,'2025-12-04 16:09:41','2025-12-04 16:09:41'),(5,2,'9789579282130','統計學：基礎與應用','商業管理類','Gerald Keller','新加坡商聖智學習亞洲私人有限公','九成','無','',100,1,'下架','待審核',NULL,'2025-12-04 16:09:41','2025-12-04 16:09:41'),(6,2,'9789865774141','管理學: 整合觀點與創新思維 (第4版)','商業管理類','中山大學企業管理學系','前程文化事業股份有限公司','九成','無','',100,1,'下架','待審核',NULL,'2025-12-04 16:09:41','2025-12-04 16:09:41'),(7,3,'9789865030612','計算機概論: 當代資訊通鑑 (第14版)','商業管理類','趙坤茂/ 張雅惠/ 黃俊穎/ 黃寶萱','全華圖書股份有限公司','八成','少量筆記','',10,1,'上架','審核通過',NULL,'2025-12-06 16:09:41','2025-12-07 11:54:52'),(8,3,'9789869462686','運籌視野之供應鏈管理 (10版)','商業管理類','John J. Coyle, C. John Langley, Jr., Robert A. Novack, Brian J. Gibson','東華','九成','無','',100,1,'上架','審核通過',NULL,'2025-12-04 16:09:41','2025-12-04 16:09:41'),(9,3,'9789865774998','資料庫的核心理論與實務 (7版)','商業管理類','黃三益','前程文化事業股份有限公司','九成','無','',100,1,'上架','審核通過',NULL,'2025-12-04 16:09:41','2025-12-04 16:09:41'),(10,3,'9789867433633','經濟學概論(三版)','商業管理類','胡春田/巫和懋/霍德明/熊秉元','雙葉書廊','九成','無','',100,1,'上架','審核通過',NULL,'2025-12-04 16:09:41','2025-12-04 16:09:41'),(11,3,'9789579282130','統計學：基礎與應用','商業管理類','Gerald Keller','新加坡商聖智學習亞洲私人有限公','九成','無','',100,1,'上架','審核通過',NULL,'2025-12-04 16:09:41','2025-12-04 16:09:41'),(12,3,'9789865774141','管理學: 整合觀點與創新思維 (第4版)','商業管理類','中山大學企業管理學系','前程文化事業股份有限公司','九成','無','',100,1,'上架','審核通過',NULL,'2025-12-04 16:09:41','2025-12-04 16:09:41'),(13,4,'9789865030612','計算機概論: 當代資訊通鑑 (第14版)','商業管理類','趙坤茂/ 張雅惠/ 黃俊穎/ 黃寶萱','全華圖書股份有限公司','一成','無','第六頁缺損',110,1,'上架','審核通過',NULL,'2025-12-01 16:09:41','2025-12-07 11:54:52'),(14,5,'9789865030612','計算機概論: 當代資訊通鑑 (第14版)','商業管理類','趙坤茂/ 張雅惠/ 黃俊穎/ 黃寶萱','全華圖書股份有限公司','六成','大量筆記','',120,1,'上架','審核通過',NULL,'2025-12-01 16:09:41','2025-12-07 11:54:52'),(15,6,'9789865030612','計算機概論: 當代資訊通鑑 (第14版)','商業管理類','趙坤茂/ 張雅惠/ 黃俊穎/ 黃寶萱','全華圖書股份有限公司','九成','無','書頁泛黃',130,1,'上架','審核通過',NULL,'2025-12-01 16:09:41','2025-12-07 11:54:52');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('會員','管理員') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '會員',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin','S87654321','學生會','管理員','2025-12-04 16:08:30','2025-12-04 16:08:30'),(2,'user001','123','S12345678','資訊管理學系','會員','2025-12-04 16:08:30','2025-12-04 16:08:30'),(3,'user002','123','S11223344','資訊管理學系','會員','2025-12-04 16:08:30','2025-12-04 16:08:30'),(4,'user003','123','s00000003','資訊管理學系','會員','2025-12-04 16:08:30','2025-12-04 16:08:30'),(5,'user004','123','s00000004','資訊管理學系','會員','2025-12-04 16:08:30','2025-12-04 16:08:30'),(6,'user005','123','s00000005','資訊管理學系','會員','2025-12-04 16:08:30','2025-12-04 16:08:30'),(7,'user006','123','s00000006','日文系','會員','2025-12-04 16:08:30','2025-12-04 16:08:30'),(8,'user007','123','s00000007','日文系','會員','2025-12-04 16:08:30','2025-12-04 16:08:30'),(9,'user008','123','s00000008','日文系','會員','2025-12-04 16:08:30','2025-12-04 16:08:30'),(10,'user009','123','s00000009','日文系','會員','2025-12-04 16:08:30','2025-12-04 16:08:30'),(11,'user010','123','s00000010','日文系','會員','2025-12-04 16:08:30','2025-12-04 16:08:30');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-07 11:57:48
