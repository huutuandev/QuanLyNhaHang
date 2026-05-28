import { FaFacebookF, FaYoutube, FaInstagram } from 'react-icons/fa';
import "./Footer.scss";

function Footer() {
  return (
    <div className="footer">
      <div className="footer__section">
        <h3 className="footer__title">Về nhà hàng</h3>
        <ul>
          <li>› Về Chúng Tôi</li>
          <li>› Liên Hệ</li>
          <li>› Dịch Vụ</li>
          <li>› Chính Sách Hoạt Động</li>
          <li>› Hướng Dẫn Đặt Bàn</li>
        </ul>
      </div>

      <div className="footer__section">
        <h3 className="footer__title">Thông tin liên lạc</h3>
        <ul>
          <li>📍 Số 82, đường Lê Bình, Quận Ninh Kiều, TP.Cần Thơ</li>
          <li>📞 078.546.8567</li>
          <li>📧 contact.huongsen@gmail.com</li>
          <li className="footer__social">
            <FaFacebookF />
            <FaInstagram />
            <FaYoutube />
          </li>
        </ul>
      </div>

      <div className="footer__section">
        <h3 className="footer__title">Giờ mở cửa</h3>
        <ul>
          <li>Thứ Hai - Thứ Sáu: 8:00 - 22:00</li>
          <li>Thứ Bảy - Chủ Nhật: 10:00 - 23:00</li>
        </ul>
      </div>

      <div className="footer__section">
        <h3 className="footer__title">Liên hệ nhanh</h3>
        <ul>
          <li>
            Nếu có thắc mắc hoặc muốn nhận thêm ưu đãi hãy liên hệ ngay với chúng tôi.
          </li>
          <li>
            <div className="footer__input">
              <input type="email" placeholder="Điền email tại đây" />
              <button>GỬI</button>
            </div>
          </li>
        </ul>
      </div>
    </div>
  );
}

export default Footer;
