-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th6 30, 2025 lúc 05:40 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `bookingcare`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `bac_sy`
--

CREATE TABLE `bac_sy` (
  `id` int(11) NOT NULL,
  `ho_ten` varchar(255) DEFAULT NULL,
  `ngay_sinh` date DEFAULT NULL,
  `sdt` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `chuc_vu` varchar(255) DEFAULT NULL,
  `tien_kham` int(11) DEFAULT NULL,
  `chuyen_khoa_id` int(11) DEFAULT NULL,
  `noi_kham` varchar(255) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `chung_chi` varchar(255) DEFAULT NULL,
  `kinh_nghiem` varchar(255) DEFAULT NULL,
  `linh_vuc_chuyen_sau` varchar(255) DEFAULT NULL,
  `id_tai_khoan` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `bac_sy`
--

INSERT INTO `bac_sy` (`id`, `ho_ten`, `ngay_sinh`, `sdt`, `email`, `chuc_vu`, `tien_kham`, `chuyen_khoa_id`, `noi_kham`, `photo`, `chung_chi`, `kinh_nghiem`, `linh_vuc_chuyen_sau`, `id_tai_khoan`) VALUES
(1, 'Phạm Văn Nam', '1978-06-15', '0908765432', 'nam.pham@gmail.com', 'PGS.TS', 350000, 1, 'Bệnh viện Chợ Rẫy', 'images/doctor_1.png', 'Chứng chỉ hành nghề Y', 'PGS.TS Phạm Văn Nam có hơn 12 năm công tác trong lĩnh vực Nội tổng quát, từng giữ vị trí trưởng khoa tại Bệnh viện Chợ Rẫy. Ông có nhiều kinh nghiệm điều trị các bệnh mạn tính như tăng huyết áp, đái tháo đường, tim mạch và các hội chứng chuyển hóa, với ph', 'Nội tổng quát', 2),
(2, 'Lê Thị Hồng', '1983-11-20', '0932145678', 'hong.le@gmail.com', 'ThS.BS', 300000, 2, 'Bệnh viện Nhi Đồng', 'images/doctor_2.png', 'Chứng chỉ chuyên khoa Nhi', 'ThS.BS Lê Thị Hồng có hơn 8 năm làm việc tại Bệnh viện Nhi Đồng, chuyên điều trị các bệnh lý hô hấp, tiêu hóa, dị ứng và các bệnh truyền nhiễm ở trẻ nhỏ. Bác sĩ nổi bật bởi sự nhẹ nhàng, tận tâm, và luôn phối hợp chặt chẽ với phụ huynh để đưa ra phác đồ đ', 'Nhi khoa', 3),
(3, 'Nguyễn Văn Hùng', '1975-03-10', '0911234567', 'hung.nguyen@gmail.com', 'BS', 320000, 3, 'Bệnh viện Bạch Mai', 'images/doctor_3.png', 'Chứng chỉ Tâm lý học', 'BS Nguyễn Văn Hùng là chuyên gia trong lĩnh vực Tâm lý lâm sàng với 15 năm kinh nghiệm tại Bệnh viện Bạch Mai. Ông từng tham gia tư vấn điều trị cho các bệnh nhân mắc rối loạn lo âu, trầm cảm, stress hậu COVID và PTSD, áp dụng liệu pháp nhận thức hành vi ', 'Tâm lý lâm sàng', 6),
(4, 'Trần Thị Lan', '1980-07-22', '0922345678', 'lan.tran@gmail.com', 'PGS.TS', 400000, 4, 'Bệnh viện Việt Đức', 'images/doctor_4.png', 'Chứng chỉ Ngoại khoa', 'PGS.TS Trần Thị Lan đã có hơn 10 năm kinh nghiệm trong lĩnh vực phẫu thuật tổng quát tại Bệnh viện Việt Đức. Bác sĩ chuyên về phẫu thuật tiêu hóa, sỏi mật, trĩ nội – ngoại và phẫu thuật nội soi. Đồng thời bà là giảng viên cao cấp và cố vấn chuyên môn tron', 'Phẫu thuật tổng quát', 7),
(5, 'Phạm Minh Tuấn', '1985-12-05', '0933456789', 'tuan.pham@gmail.com', 'ThS.BS', 280000, 5, 'Bệnh viện Da liễu TP.HCM', 'images/doctor_5.png', 'Chứng chỉ Da liễu', 'ThS.BS Phạm Minh Tuấn có 7 năm kinh nghiệm điều trị da liễu và thẩm mỹ nội khoa tại Bệnh viện Da liễu TP.HCM. Anh am hiểu các kỹ thuật laser trị nám, lăn kim tế bào gốc, PRP và xử lý các vấn đề như mụn viêm, sẹo rỗ, tàn nhang bằng các phương pháp hiện đại', 'Da liễu thẩm mỹ', 8),
(6, 'Đỗ Thị Thanh', '1979-01-15', '0944567890', 'thanh.do@gmail.com', 'BS', 300000, 6, 'Bệnh viện Mắt TW', 'images/doctor_6.png', 'Chứng chỉ Nhãn khoa', 'BS Đỗ Thị Thanh có 9 năm kinh nghiệm điều trị các bệnh lý nhãn khoa tại Bệnh viện Mắt TW. Bác sĩ chuyên khám và điều trị các bệnh lý như viêm kết mạc, cận thị, đục thủy tinh thể và đã thực hiện hàng trăm ca mổ mắt nội soi, mang lại ánh sáng cho hàng ngàn ', 'Nhãn khoa', 9),
(7, 'Lê Văn Tâm', '1982-08-30', '0955678901', 'tam.le@gmail.com', 'ThS.BS', 310000, 7, 'Bệnh viện Tai Mũi Họng TW', 'images/doctor_7.png', 'Chứng chỉ Tai mũi họng', 'ThS.BS Lê Văn Tâm có hơn 8 năm kinh nghiệm trong chuyên ngành Tai – Mũi – Họng tại Bệnh viện Tai Mũi Họng TW. Bác sĩ chuyên điều trị viêm xoang mãn tính, viêm tai giữa, viêm họng hạt và đã thực hiện nhiều ca phẫu thuật nội soi mũi xoang thành công.', 'Tai mũi họng', 10),
(8, 'Hoàng Thị Mai', '1987-04-18', '0966789012', 'mai.hoang@gmail.com', 'BS', 350000, 8, 'Bệnh viện Răng Hàm Mặt', 'images/doctor_8.png', 'Chứng chỉ Răng hàm mặt', 'BS Hoàng Thị Mai là bác sĩ Răng Hàm Mặt với hơn 6 năm kinh nghiệm tại Bệnh viện Răng Hàm Mặt TP.HCM. Bác sĩ chuyên về điều trị viêm nha chu, tẩy trắng răng, phục hình răng thẩm mỹ và niềng răng không mắc cài Invisalign.', 'Răng hàm mặt', 11),
(9, 'Vũ Minh Đức', '1976-09-12', '0977890123', 'duc.vu@gmail.com', 'PGS.TS', 380000, 9, 'Bệnh viện Phụ sản TW', 'images/doctor_9.png', 'Chứng chỉ Phụ khoa', 'PGS.TS Vũ Minh Đức đã có 14 năm hoạt động trong lĩnh vực Phụ khoa tại Bệnh viện Phụ sản TW. Bác sĩ có nhiều năm nghiên cứu và thực hành chuyên sâu về các bệnh lý u xơ tử cung, lạc nội mạc tử cung, điều trị vô sinh – hiếm muộn, phẫu thuật nội soi buồng trứ', 'Phụ khoa', 12),
(10, 'Nguyễn Thị Minh', '1981-02-25', '0988901234', 'minh.nguyen@gmail.com', 'BS', 330000, 1, 'Bệnh viện Chợ Rẫy', 'images/doctor_10.png', 'Chứng chỉ Nội khoa', 'BS Nguyễn Thị Minh có 10 năm kinh nghiệm làm việc trong lĩnh vực Nội tổng quát tại Bệnh viện Chợ Rẫy. Bác sĩ thường xuyên điều trị bệnh nhân mắc các bệnh lý tim mạch, đái tháo đường và rối loạn lipid máu. Bác sĩ Minh luôn theo sát bệnh nhân trong từng gia', 'Nội tổng quát', 13),
(11, 'Trần Văn Hoàng', '1977-05-14', '0999012345', 'hoang.tran@gmail.com', 'PGS.TS', 360000, 2, 'Bệnh viện Nhi Đồng 2', 'images/doctor_11.png', 'Chứng chỉ Nhi khoa', 'PGS.TS Trần Văn Hoàng là chuyên gia Nhi khoa với 13 năm kinh nghiệm tại Bệnh viện Nhi Đồng 2. Ông là người đầu tiên áp dụng thành công kỹ thuật xét nghiệm gene để chẩn đoán bệnh di truyền ở trẻ sơ sinh tại bệnh viện.', 'Nhi khoa', 14),
(12, 'Lê Thị Ngọc', '1984-09-30', '0900123456', 'ngoc.le@gmail.com', 'ThS.BS', 310000, 3, 'Bệnh viện Tâm thần TW', 'images/doctor_12.png', 'Chứng chỉ Tâm lý học', 'ThS.BS Lê Thị Ngọc đã có 9 năm công tác trong ngành Tâm lý lâm sàng tại Bệnh viện Tâm thần TW. Bác sĩ có thế mạnh về trị liệu cá nhân, trị liệu gia đình và hỗ trợ trẻ em mắc rối loạn phát triển, tự kỷ và rối loạn tăng động.', 'Tâm lý lâm sàng', 15),
(13, 'Phạm Văn An', '1980-12-10', '0911237890', 'an.pham@gmail.com', 'BS', 290000, 4, 'Bệnh viện Việt Đức', 'images/doctor_13.png', 'Chứng chỉ Ngoại khoa', 'BS Phạm Văn An có 8 năm kinh nghiệm tại Bệnh viện Việt Đức chuyên về phẫu thuật tổng quát. Ông đã thực hiện nhiều ca mổ tiêu hóa, mổ ruột thừa, thoát vị bẹn và hỗ trợ điều trị chấn thương bụng kín do tai nạn giao thông.', 'Phẫu thuật tổng quát', 16),
(14, 'Đỗ Minh Khang', '1986-07-07', '0922348901', 'khang.do@gmail.com', 'ThS.BS', 340000, 5, 'Bệnh viện Da liễu TW', 'images/doctor_14.png', 'Chứng chỉ Da liễu', 'ThS.BS Đỗ Minh Khang là bác sĩ Da liễu với 7 năm kinh nghiệm tại Bệnh viện Da liễu TW. Anh nổi bật trong điều trị viêm da cơ địa, nấm da, điều trị nám và các phương pháp trẻ hóa da bằng công nghệ cao như laser CO2, RF.', 'Da liễu thẩm mỹ', 17),
(15, 'Nguyễn Thị Hà', '1979-03-22', '0933459012', 'ha.nguyen@gmail.com', 'PGS.TS', 370000, 6, 'Bệnh viện Mắt TP.HCM', 'images/doctor_15.png', 'Chứng chỉ Nhãn khoa', 'PGS.TS Nguyễn Thị Hà đã có 12 năm làm việc trong lĩnh vực Nhãn khoa tại Bệnh viện Mắt TP.HCM. Bà là chuyên gia trong phẫu thuật đục thủy tinh thể bằng phương pháp Phaco, điều trị võng mạc tiểu đường và khúc xạ học.', 'Nhãn khoa', 18),
(16, 'Trần Văn Long', '1982-11-11', '0944560123', 'long.tran@gmail.com', 'BS', 300000, 7, 'Bệnh viện Tai Mũi Họng TP.HCM', 'images/doctor_16.png', 'Chứng chỉ Tai mũi họng', 'BS Trần Văn Long có 9 năm kinh nghiệm tại Bệnh viện Tai Mũi Họng TP.HCM. Bác sĩ thường xuyên điều trị các ca viêm xoang mạn tính, dị ứng mũi theo mùa, polyp mũi và điều trị các rối loạn giọng nói do viêm thanh quản.', 'Tai mũi họng', 19),
(17, 'Lê Thị Thủy', '1985-06-18', '0955671234', 'thuy.le@gmail.com', 'ThS.BS', 320000, 8, 'Bệnh viện Răng Hàm Mặt TP.HCM', 'images/doctor_17.png', 'Chứng chỉ Răng hàm mặt', 'ThS.BS Lê Thị Thủy là bác sĩ Răng Hàm Mặt có 8 năm kinh nghiệm, chuyên điều trị sâu răng, viêm lợi, phục hình răng sứ, implant và tư vấn chăm sóc răng miệng cho trẻ em. Bác sĩ luôn tạo cảm giác thoải mái cho người bệnh trong quá trình điều trị.', 'Răng hàm mặt', 20),
(18, 'Hoàng Văn Phong', '1978-04-05', '0966782345', 'phong.hoang@gmail.com', 'PGS.TS', 390000, 9, 'Bệnh viện Phụ sản Hà Nội', 'images/doctor_18.png', 'Chứng chỉ Phụ khoa', 'PGS.TS Hoàng Văn Phong là chuyên gia Phụ khoa với 15 năm kinh nghiệm tại Bệnh viện Phụ sản Hà Nội. Bác sĩ đã điều trị thành công nhiều ca bệnh lý buồng trứng, rối loạn nội tiết, hiếm muộn và có nhiều công trình nghiên cứu trong lĩnh vực nội tiết sinh sản.', 'Phụ khoa', 21),
(19, 'Vũ Thị Lan', '1983-08-20', '0977893456', 'lan.vu@gmail.com', 'BS', 310000, 1, 'Bệnh viện Bạch Mai', 'images/doctor_19.png', 'Chứng chỉ Nội khoa', 'BS Vũ Thị Lan có 10 năm kinh nghiệm trong lĩnh vực Nội tổng quát tại Bệnh viện Bạch Mai. Bác sĩ có thế mạnh trong điều trị bệnh lý chuyển hóa, rối loạn nội tiết, bệnh về gan mật và từng tham gia các chương trình đào tạo bác sĩ tuyến huyện.', 'Nội tổng quát', 37),
(20, 'Phạm Văn Huy', '1980-01-15', '0988904567', 'huy.pham@gmail.com', 'ThS.BS', 340000, 2, 'Bệnh viện Nhi TW', 'images/doctor_20.png', 'Chứng chỉ Nhi khoa', 'ThS.BS Phạm Văn Huy đã có 9 năm làm việc tại Bệnh viện Nhi TW. Anh chuyên điều trị sốt cao co giật, viêm phổi, hen suyễn và các bệnh truyền nhiễm. Bác sĩ Huy được phụ huynh yêu mến bởi phong thái nhẹ nhàng, tư vấn kỹ lưỡng và dễ hiểu.', 'Nhi khoa', 23),
(21, 'Nguyễn Minh Tuấn', '1976-10-10', '0999015678', 'tuan.nguyen2@gmail.com', 'PGS.TS', 380000, 3, 'Bệnh viện Tâm thần TP.HCM', 'images/doctor_21.png', 'Chứng chỉ Tâm lý học', 'PGS.TS Nguyễn Minh Tuấn là bác sĩ Tâm lý lâm sàng với 14 năm kinh nghiệm tại Bệnh viện Tâm thần TP.HCM. Bác sĩ có chuyên môn cao trong điều trị các rối loạn tâm thần, tâm lý học đường, stress nghề nghiệp và thường xuyên được mời tư vấn truyền hình, báo ch', 'Tâm lý lâm sàng', 24);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `bai_dang`
--

CREATE TABLE `bai_dang` (
  `id` int(11) NOT NULL,
  `ten` varchar(255) DEFAULT NULL,
  `noi_dung` text DEFAULT NULL,
  `tom_tat` text DEFAULT NULL,
  `chuyen_muc_id` int(11) DEFAULT NULL,
  `create_at` datetime DEFAULT NULL,
  `writer_by` varchar(255) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `bai_dang`
--

INSERT INTO `bai_dang` (`id`, `ten`, `noi_dung`, `tom_tat`, `chuyen_muc_id`, `create_at`, `writer_by`, `photo`) VALUES
(1, 'Hiểu biết về sức khỏe tâm lý', 'Bài viết chia sẻ các phương pháp cải thiện sức khỏe tâm lý...', 'Mẹo chăm sóc sức khỏe tâm lý', 3, '2025-06-01 08:00:00', 'doctor01', 'mental_health.jpg'),
(2, 'Lợi ích của chế độ ăn uống lành mạnh', 'Nội dung về tầm quan trọng của dinh dưỡng đối với sức khỏe...', 'Tầm quan trọng của dinh dưỡng', 2, '2025-06-01 09:00:00', 'admin01', 'nutrition.jpg');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `benh_nhan`
--

CREATE TABLE `benh_nhan` (
  `id` int(11) NOT NULL,
  `ho_ten` varchar(255) DEFAULT NULL,
  `ngay_sinh` date DEFAULT NULL,
  `sdt` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `dia_chi` varchar(255) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `id_tai_khoan` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `benh_nhan`
--

INSERT INTO `benh_nhan` (`id`, `ho_ten`, `ngay_sinh`, `sdt`, `email`, `dia_chi`, `photo`, `id_tai_khoan`) VALUES
(1, 'Nguyễn Thị Mai', '1995-04-12', '0912345678', 'quanglinhwork04@gmail.com', '123 Lê Lợi, Hà Nội', 'patient01.jpg', 4),
(2, 'Trần Văn Hùng', '1988-09-25', '0987654321', 'adodareview@gmail.com', '456 Nguyễn Huệ, TP.HCM', 'patient02.jpg', 5),
(3, 'Phạm Thị Lan', '1990-02-14', '0913456789', 'adodareview@gmail.com', '789 Trần Hưng Đạo, Đà Nẵng', 'patient03.jpg', 16),
(4, 'Lê Văn Quang', '1985-06-20', '0924567890', 'quang.le@gmail.com', '101 Nguyễn Trãi, Hà Nội', 'patient04.jpg', 17),
(5, 'Trần Thị Hoa', '1992-11-08', '0935678901', 'hoa.tran@gmail.com', '202 Phạm Văn Đồng, TP.HCM', 'patient05.jpg', 18),
(6, 'Nguyễn Minh Tuấn', '1987-03-25', '0946789012', 'tuan.nguyen@gmail.com', '303 Lê Lợi, Huế', 'patient06.jpg', 19),
(7, 'Đỗ Thị Ngọc', '1993-08-17', '0957890123', 'ngoc.do@gmail.com', '404 Nguyễn Huệ, Nha Trang', 'patient07.jpg', 20),
(8, 'Hoàng Văn Nam', '1989-12-30', '0968901234', 'nam.hoang@gmail.com', '505 Đống Đa, Hà Nội', 'patient08.jpg', 21),
(9, 'Vũ Thị Thảo', '1994-05-10', '0979012345', 'thao.vu@gmail.com', '606 Điện Biên Phủ, TP.HCM', 'patient09.jpg', 22);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `binh_luan`
--

CREATE TABLE `binh_luan` (
  `id` int(11) NOT NULL,
  `noi_dung` text DEFAULT NULL,
  `id_bac_sy` int(11) DEFAULT NULL,
  `id_benh_nhan` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `binh_luan`
--

INSERT INTO `binh_luan` (`id`, `noi_dung`, `id_bac_sy`, `id_benh_nhan`) VALUES
(1, 'Bác sĩ rất nhiệt tình và chuyên nghiệp!', 1, 1),
(2, 'Bác sĩ Hồng khám rất cẩn thận, giải thích dễ hiểu.', 2, 2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chi_tiet_toa_thuoc`
--

CREATE TABLE `chi_tiet_toa_thuoc` (
  `id` int(11) NOT NULL,
  `toa_thuoc_id` int(11) NOT NULL,
  `thuoc_id` int(11) NOT NULL,
  `don_vi_tinh` varchar(50) NOT NULL,
  `so_luong` int(11) NOT NULL,
  `hdsd` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chuyen_khoa`
--

CREATE TABLE `chuyen_khoa` (
  `id` int(11) NOT NULL,
  `ten` varchar(255) DEFAULT NULL,
  `mo_ta` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `chuyen_khoa`
--

INSERT INTO `chuyen_khoa` (`id`, `ten`, `mo_ta`) VALUES
(1, 'Nội khoa', 'Chuyên khoa điều trị các bệnh nội khoa'),
(2, 'Nhi khoa', 'Chuyên khoa điều trị các bệnh trẻ em'),
(3, 'Tâm lý học', 'Chuyên khoa điều trị các vấn đề tâm lý'),
(4, 'Ngoại khoa', 'Chuyên khoa phẫu thuật và điều trị ngoại khoa'),
(5, 'Da liễu', 'Chuyên khoa điều trị các bệnh về da'),
(6, 'Nhãn khoa', 'Chuyên khoa điều trị các bệnh về mắt'),
(7, 'Tai mũi họng', 'Chuyên khoa điều trị các bệnh tai, mũi, họng'),
(8, 'Răng hàm mặt', 'Chuyên khoa điều trị các bệnh về răng và hàm'),
(9, 'Phụ khoa', 'Chuyên khoa điều trị các bệnh phụ nữ'),
(10, 'Tiêu hóa', 'Chuyên khoa điều trị các bệnh đường tiêu hóa');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chuyen_muc`
--

CREATE TABLE `chuyen_muc` (
  `id` int(11) NOT NULL,
  `ten` varchar(255) DEFAULT NULL,
  `mo_ta` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `chuyen_muc`
--

INSERT INTO `chuyen_muc` (`id`, `ten`, `mo_ta`) VALUES
(1, 'Sức khỏe tổng quát', 'Thông tin về sức khỏe tổng quát và phòng bệnh'),
(2, 'Dinh dưỡng', 'Kiến thức về dinh dưỡng và chế độ ăn uống'),
(3, 'Sức khỏe tâm lý', 'Thông tin về sức khỏe tâm lý và tinh thần');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `lich_kham`
--

CREATE TABLE `lich_kham` (
  `id` int(11) NOT NULL,
  `chuyen_khoa_id` int(11) DEFAULT NULL,
  `mo_ta_trieu_chung` text DEFAULT NULL,
  `thoi_gian_dk` datetime DEFAULT NULL,
  `ngay_kham` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `khung_gio_kham` int(11) DEFAULT NULL,
  `tien_kham` int(11) DEFAULT NULL,
  `id_bac_sy` int(11) DEFAULT NULL,
  `id_benh_nhan` int(11) DEFAULT NULL,
  `chan_doan` text DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `lich_kham`
--

INSERT INTO `lich_kham` (`id`, `chuyen_khoa_id`, `mo_ta_trieu_chung`, `thoi_gian_dk`, `ngay_kham`, `status`, `khung_gio_kham`, `tien_kham`, `id_bac_sy`, `id_benh_nhan`, `chan_doan`, `order_number`) VALUES
(95, 1, 'hhhhh', '2025-06-30 21:12:19', '2025-07-02', '1', 4, 350000, 1, 2, NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `lich_kham_bac_sy`
--

CREATE TABLE `lich_kham_bac_sy` (
  `id` bigint(20) NOT NULL,
  `ma_bac_sy` int(11) NOT NULL,
  `ngay_trong_tuan` varchar(10) NOT NULL,
  `da_san_sang` tinyint(1) NOT NULL DEFAULT 1,
  `thoi_gian_tao` datetime NOT NULL DEFAULT current_timestamp(),
  `thoi_gian_cap_nhat` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `ngay_kham` date NOT NULL,
  `khung_gio` varchar(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `lich_kham_bac_sy`
--

INSERT INTO `lich_kham_bac_sy` (`id`, `ma_bac_sy`, `ngay_trong_tuan`, `da_san_sang`, `thoi_gian_tao`, `thoi_gian_cap_nhat`, `ngay_kham`, `khung_gio`) VALUES
(3, 1, 'Thứ Tư', 1, '2025-06-30 18:14:26', '2025-06-30 18:14:26', '2025-07-02', '10:30-11:00'),
(4, 1, 'Thứ Tư', 1, '2025-06-30 18:14:26', '2025-06-30 18:14:26', '2025-07-02', '09:30-10:00'),
(5, 1, 'Thứ Tư', 1, '2025-06-30 18:14:26', '2025-06-30 18:14:26', '2025-07-02', '10:00-10:30'),
(6, 1, 'Thứ Tư', 1, '2025-06-30 18:14:26', '2025-06-30 18:14:26', '2025-07-02', '16:00-16:30'),
(7, 1, 'Thứ Tư', 1, '2025-06-30 18:14:26', '2025-06-30 18:14:26', '2025-07-02', '08:30-09:00'),
(8, 1, 'Thứ Tư', 1, '2025-06-30 18:14:26', '2025-06-30 18:14:26', '2025-07-02', '08:00-08:30'),
(9, 1, 'Thứ Sáu', 1, '2025-06-30 18:19:13', '2025-06-30 18:19:13', '2025-07-04', '08:00-08:30'),
(10, 1, 'Thứ Sáu', 1, '2025-06-30 18:19:13', '2025-06-30 18:19:13', '2025-07-04', '09:00-09:30'),
(11, 1, 'Thứ Sáu', 1, '2025-06-30 18:19:13', '2025-06-30 18:19:13', '2025-07-04', '08:30-09:00'),
(12, 1, 'Thứ Năm', 1, '2025-06-30 18:20:21', '2025-06-30 18:20:21', '2025-07-03', '09:30-10:00'),
(13, 1, 'Thứ Năm', 1, '2025-06-30 18:20:21', '2025-06-30 18:20:21', '2025-07-03', '10:00-10:30'),
(14, 1, 'Thứ Năm', 1, '2025-06-30 18:20:21', '2025-06-30 18:20:21', '2025-07-03', '10:30-11:00'),
(15, 1, 'Thứ Năm', 1, '2025-06-30 18:20:21', '2025-06-30 18:20:21', '2025-07-03', '14:00-14:30'),
(16, 1, 'Thứ Năm', 1, '2025-06-30 18:20:21', '2025-06-30 18:20:21', '2025-07-03', '11:00-11:30'),
(17, 1, 'Thứ Năm', 1, '2025-06-30 18:20:21', '2025-06-30 18:20:21', '2025-07-03', '13:30-14:00'),
(18, 1, 'Thứ Năm', 1, '2025-06-30 18:20:21', '2025-06-30 18:20:21', '2025-07-03', '15:00-15:30'),
(19, 1, 'Thứ Năm', 1, '2025-06-30 18:20:21', '2025-06-30 18:20:21', '2025-07-03', '14:30-15:00'),
(20, 1, 'Thứ Năm', 1, '2025-06-30 18:20:21', '2025-06-30 18:20:21', '2025-07-03', '15:30-16:00'),
(21, 1, 'Thứ Năm', 1, '2025-06-30 18:20:21', '2025-06-30 18:20:21', '2025-07-03', '16:00-16:30'),
(22, 1, 'Thứ Năm', 1, '2025-06-30 18:20:21', '2025-06-30 18:20:21', '2025-07-03', '16:30-17:00');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `luot_kham`
--

CREATE TABLE `luot_kham` (
  `id` int(11) NOT NULL,
  `ma_dat_lich` int(11) DEFAULT NULL,
  `id_bac_sy` int(11) DEFAULT NULL,
  `id_benh_nhan` int(11) DEFAULT NULL,
  `ten_benh_nhan` varchar(255) DEFAULT NULL,
  `ngay_sinh_benh_nhan` date DEFAULT NULL,
  `ly_do_kham` varchar(255) DEFAULT NULL,
  `sdt_benh_nhan` varchar(20) DEFAULT NULL,
  `so_thu_tu` int(11) DEFAULT NULL,
  `vi_tri` int(11) DEFAULT NULL,
  `gio_kham` datetime DEFAULT NULL,
  `ngay_kham` date DEFAULT NULL,
  `trang_thai` varchar(50) DEFAULT NULL,
  `ngay_tao` datetime DEFAULT NULL,
  `ngay_cap_nhat` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tai_khoan`
--

CREATE TABLE `tai_khoan` (
  `id` int(11) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `id_vai_tro` int(11) DEFAULT NULL,
  `provider` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `tai_khoan`
--

INSERT INTO `tai_khoan` (`id`, `username`, `password`, `id_vai_tro`, `provider`) VALUES
(1, 'admin01', '123', 1, 1),
(2, 'doctor01', '123', 2, 1),
(3, 'doctor02', '123', 2, 1),
(4, 'patient01', '123', 3, 1),
(5, 'patient02', '123', 3, 1),
(6, 'doctor03', '123', 2, 1),
(7, 'doctor04', '123', 2, 1),
(8, 'doctor05', '123', 2, 1),
(9, 'doctor06', '123', 2, 1),
(10, 'doctor07', '123', 2, 1),
(11, 'doctor08', '123', 2, 1),
(12, 'doctor09', '123', 2, 1),
(13, 'doctor10', '123', 2, 1),
(14, 'doctor11', '123', 2, 1),
(15, 'doctor12', '123', 2, 1),
(16, 'patient03', '123', 3, 1),
(17, 'patient04', '123', 3, 1),
(18, 'patient05', '123', 3, 1),
(19, 'patient06', '123', 3, 1),
(20, 'patient07', '123', 3, 1),
(21, 'patient08', '123', 3, 1),
(22, 'patient09', '123', 3, 1),
(23, 'patient10', '123', 3, 1),
(24, 'patient11', '123', 3, 1),
(25, 'patient12', '123', 3, 1),
(26, 'doctor13', '123', 2, 1),
(27, 'doctor14', '123', 2, 1),
(28, 'doctor15', '123', 2, 1),
(29, 'doctor16', '123', 2, 1),
(30, 'doctor17', '123', 2, 1),
(31, 'doctor18', '123', 2, 1),
(32, 'doctor19', '123', 2, 1),
(33, 'doctor20', '123', 2, 1),
(34, 'doctor21', '123', 2, 1),
(35, 'doctor22', '123', 2, 1),
(36, 'doctor23', '123', 2, 1),
(37, 'doctor24', '123', 2, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `thuoc`
--

CREATE TABLE `thuoc` (
  `id` int(11) NOT NULL,
  `ten_thuoc` varchar(255) NOT NULL,
  `don_vi` varchar(50) DEFAULT NULL,
  `mo_ta` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `thuoc`
--

INSERT INTO `thuoc` (`id`, `ten_thuoc`, `don_vi`, `mo_ta`) VALUES
(7, 'Paracetamol 500mg', 'viên', 'Thuốc hạ sốt, giảm đau. Dùng trong các trường hợp cảm cúm, đau đầu.'),
(8, 'Amoxicillin 500mg', 'viên', 'Kháng sinh nhóm penicillin. Dùng trị nhiễm trùng hô hấp, da, răng miệng.'),
(9, 'Vitamin C 1000mg', 'viên', 'Bổ sung vitamin C, tăng sức đề kháng.'),
(10, 'Cotrimoxazol', 'viên', 'Kháng sinh phổ rộng. Dùng điều trị nhiễm khuẩn đường tiết niệu, tiêu hóa.'),
(11, 'Salonpas', 'miếng', 'Miếng dán giảm đau vai gáy, cơ bắp. Không dùng trên vết thương hở.'),
(12, 'Methotrexate', 'viên', 'Thuốc điều trị viêm khớp dạng thấp, ung thư. Cần theo dõi chức năng gan thận.'),
(13, 'Omeprazole 20mg', 'viên', 'Điều trị viêm loét dạ dày, trào ngược dạ dày thực quản.'),
(14, 'Acemol', 'viên', 'Thuốc giảm đau, hạ sốt. Giống Paracetamol.'),
(15, 'Efferalgan Codein', 'viên sủi', 'Hạ sốt mạnh, giảm đau vừa phải. Có chứa Codein gây buồn ngủ.'),
(16, 'Smecta', 'gói', 'Điều trị tiêu chảy cấp và mãn tính. Có thể dùng cho trẻ em.');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `toa_thuoc`
--

CREATE TABLE `toa_thuoc` (
  `id` int(11) NOT NULL,
  `lich_kham_id` int(11) NOT NULL,
  `benh_nhan_id` int(11) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `vai_tro`
--

CREATE TABLE `vai_tro` (
  `id` int(11) NOT NULL,
  `ten` varchar(255) DEFAULT NULL,
  `mo_ta` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `vai_tro`
--

INSERT INTO `vai_tro` (`id`, `ten`, `mo_ta`) VALUES
(1, 'ADMIN', 'Quản trị viên hệ thống'),
(2, 'BAC_SY', 'Bác sĩ chuyên môn'),
(3, 'BENH_NHAN', 'Bệnh nhân');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `bac_sy`
--
ALTER TABLE `bac_sy`
  ADD PRIMARY KEY (`id`),
  ADD KEY `chuyen_khoa_id` (`chuyen_khoa_id`),
  ADD KEY `id_tai_khoan` (`id_tai_khoan`);

--
-- Chỉ mục cho bảng `bai_dang`
--
ALTER TABLE `bai_dang`
  ADD PRIMARY KEY (`id`),
  ADD KEY `chuyen_muc_id` (`chuyen_muc_id`);

--
-- Chỉ mục cho bảng `benh_nhan`
--
ALTER TABLE `benh_nhan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_tai_khoan` (`id_tai_khoan`);

--
-- Chỉ mục cho bảng `binh_luan`
--
ALTER TABLE `binh_luan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_bac_sy` (`id_bac_sy`),
  ADD KEY `id_benh_nhan` (`id_benh_nhan`);

--
-- Chỉ mục cho bảng `chi_tiet_toa_thuoc`
--
ALTER TABLE `chi_tiet_toa_thuoc`
  ADD PRIMARY KEY (`id`),
  ADD KEY `toa_thuoc_id` (`toa_thuoc_id`),
  ADD KEY `thuoc_id` (`thuoc_id`);

--
-- Chỉ mục cho bảng `chuyen_khoa`
--
ALTER TABLE `chuyen_khoa`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `chuyen_muc`
--
ALTER TABLE `chuyen_muc`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `lich_kham`
--
ALTER TABLE `lich_kham`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_bac_sy` (`id_bac_sy`),
  ADD KEY `id_benh_nhan` (`id_benh_nhan`);

--
-- Chỉ mục cho bảng `lich_kham_bac_sy`
--
ALTER TABLE `lich_kham_bac_sy`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ma_bac_sy` (`ma_bac_sy`);

--
-- Chỉ mục cho bảng `luot_kham`
--
ALTER TABLE `luot_kham`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_bac_sy` (`id_bac_sy`),
  ADD KEY `id_benh_nhan` (`id_benh_nhan`);

--
-- Chỉ mục cho bảng `tai_khoan`
--
ALTER TABLE `tai_khoan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_vai_tro` (`id_vai_tro`);

--
-- Chỉ mục cho bảng `thuoc`
--
ALTER TABLE `thuoc`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `toa_thuoc`
--
ALTER TABLE `toa_thuoc`
  ADD PRIMARY KEY (`id`),
  ADD KEY `lich_kham_id` (`lich_kham_id`),
  ADD KEY `benh_nhan_id` (`benh_nhan_id`);

--
-- Chỉ mục cho bảng `vai_tro`
--
ALTER TABLE `vai_tro`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `bac_sy`
--
ALTER TABLE `bac_sy`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT cho bảng `bai_dang`
--
ALTER TABLE `bai_dang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `benh_nhan`
--
ALTER TABLE `benh_nhan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT cho bảng `binh_luan`
--
ALTER TABLE `binh_luan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `chi_tiet_toa_thuoc`
--
ALTER TABLE `chi_tiet_toa_thuoc`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT cho bảng `chuyen_khoa`
--
ALTER TABLE `chuyen_khoa`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT cho bảng `chuyen_muc`
--
ALTER TABLE `chuyen_muc`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `lich_kham`
--
ALTER TABLE `lich_kham`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=96;

--
-- AUTO_INCREMENT cho bảng `lich_kham_bac_sy`
--
ALTER TABLE `lich_kham_bac_sy`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT cho bảng `luot_kham`
--
ALTER TABLE `luot_kham`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `tai_khoan`
--
ALTER TABLE `tai_khoan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- AUTO_INCREMENT cho bảng `thuoc`
--
ALTER TABLE `thuoc`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT cho bảng `toa_thuoc`
--
ALTER TABLE `toa_thuoc`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT cho bảng `vai_tro`
--
ALTER TABLE `vai_tro`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `bac_sy`
--
ALTER TABLE `bac_sy`
  ADD CONSTRAINT `bac_sy_ibfk_1` FOREIGN KEY (`chuyen_khoa_id`) REFERENCES `chuyen_khoa` (`id`),
  ADD CONSTRAINT `bac_sy_ibfk_2` FOREIGN KEY (`id_tai_khoan`) REFERENCES `tai_khoan` (`id`);

--
-- Các ràng buộc cho bảng `bai_dang`
--
ALTER TABLE `bai_dang`
  ADD CONSTRAINT `bai_dang_ibfk_1` FOREIGN KEY (`chuyen_muc_id`) REFERENCES `chuyen_muc` (`id`);

--
-- Các ràng buộc cho bảng `benh_nhan`
--
ALTER TABLE `benh_nhan`
  ADD CONSTRAINT `benh_nhan_ibfk_1` FOREIGN KEY (`id_tai_khoan`) REFERENCES `tai_khoan` (`id`);

--
-- Các ràng buộc cho bảng `binh_luan`
--
ALTER TABLE `binh_luan`
  ADD CONSTRAINT `binh_luan_ibfk_1` FOREIGN KEY (`id_bac_sy`) REFERENCES `bac_sy` (`id`),
  ADD CONSTRAINT `binh_luan_ibfk_2` FOREIGN KEY (`id_benh_nhan`) REFERENCES `benh_nhan` (`id`);

--
-- Các ràng buộc cho bảng `chi_tiet_toa_thuoc`
--
ALTER TABLE `chi_tiet_toa_thuoc`
  ADD CONSTRAINT `chi_tiet_toa_thuoc_ibfk_1` FOREIGN KEY (`toa_thuoc_id`) REFERENCES `toa_thuoc` (`id`),
  ADD CONSTRAINT `chi_tiet_toa_thuoc_ibfk_2` FOREIGN KEY (`thuoc_id`) REFERENCES `thuoc` (`id`);

--
-- Các ràng buộc cho bảng `lich_kham`
--
ALTER TABLE `lich_kham`
  ADD CONSTRAINT `lich_kham_ibfk_1` FOREIGN KEY (`id_bac_sy`) REFERENCES `bac_sy` (`id`),
  ADD CONSTRAINT `lich_kham_ibfk_2` FOREIGN KEY (`id_benh_nhan`) REFERENCES `benh_nhan` (`id`);

--
-- Các ràng buộc cho bảng `lich_kham_bac_sy`
--
ALTER TABLE `lich_kham_bac_sy`
  ADD CONSTRAINT `lich_kham_bac_sy_ibfk_1` FOREIGN KEY (`ma_bac_sy`) REFERENCES `bac_sy` (`id`);

--
-- Các ràng buộc cho bảng `luot_kham`
--
ALTER TABLE `luot_kham`
  ADD CONSTRAINT `luot_kham_ibfk_1` FOREIGN KEY (`id_bac_sy`) REFERENCES `bac_sy` (`id`),
  ADD CONSTRAINT `luot_kham_ibfk_2` FOREIGN KEY (`id_benh_nhan`) REFERENCES `benh_nhan` (`id`);

--
-- Các ràng buộc cho bảng `tai_khoan`
--
ALTER TABLE `tai_khoan`
  ADD CONSTRAINT `tai_khoan_ibfk_1` FOREIGN KEY (`id_vai_tro`) REFERENCES `vai_tro` (`id`);

--
-- Các ràng buộc cho bảng `toa_thuoc`
--
ALTER TABLE `toa_thuoc`
  ADD CONSTRAINT `toa_thuoc_ibfk_1` FOREIGN KEY (`lich_kham_id`) REFERENCES `lich_kham` (`id`),
  ADD CONSTRAINT `toa_thuoc_ibfk_2` FOREIGN KEY (`benh_nhan_id`) REFERENCES `benh_nhan` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
