import { NavLink, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import Modal from "react-modal";
import "./Table.scss";
import axios from "axios";
function Table() {
  const navigate = useNavigate();

  // Trạng thái form đặt bàn
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    date: "",
    numberOfPeople: "",
    phone: "",
    note: "",
  });

  // Trạng thái hiển thị modal cảnh báo
  const [showLoginModal, setShowLoginModal] = useState(false);
  const getMinDateTime = () => {
    const now = new Date();
    now.setHours(now.getHours() + 4);

    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0");
    const day = String(now.getDate()).padStart(2, "0");
    const hour = String(now.getHours()).padStart(2, "0");
    const minute = String(now.getMinutes()).padStart(2, "0");
    return `${year}-${month}-${day}T${hour}:${minute}`;
  }
  const minDateTime = getMinDateTime();
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      setShowLoginModal(true);
    }
    axios.get("/api/users/me", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => {
        const user = res.data;
        setFormData((prev) => ({
          ...prev,
          name: user.fullname || "",
          email: user.email || "",
          phone: user.phoneNumber || "",
        }));
      })
      .catch((err) => {
        console.error("Lỗi lấy thông tin người dùng:", err);
        setShowLoginModal(true); // Nếu token hết hạn
      });
  }, []);

  const handleConfirmLogin = () => {
    setShowLoginModal(false);
    navigate("/login");
  };

  const handleCancelLogin = () => {
    setShowLoginModal(false);
    navigate("/");
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const { name, email, date, numberOfPeople, phone } = formData;
    if (!name || !date || !numberOfPeople || !phone) {
      alert("Vui lòng nhập đầy đủ tất cả thông tin bắt buộc!");
      return;
    }
    const selectedDateTime = new Date(date);
    const nowPlus4Hours = new Date();
    nowPlus4Hours.setHours(nowPlus4Hours.getHours() + 4);

    if (selectedDateTime < nowPlus4Hours) {
      alert("Bạn phải đặt bàn ít nhất trước 4 tiếng so với thời điểm hiện tại.");
      return;
    }
    const [datePart, timePart] = formData.date.split("T");

    const updatedFormData = {
      ...formData,
      date: datePart,
      time: timePart ? `${timePart}:00` : "",
    };

    localStorage.setItem("reservation", JSON.stringify(updatedFormData));
    navigate("/SelectTable");
  };


  return (
    <>
      <div className="Main_table">
        <section className="content1-table">
          <div className="content1-table__text">
            <h1>Đặt bàn online</h1>
            <NavLink to="/">Trang Chủ /</NavLink>
            <NavLink to="/Table"> Đặt Bàn</NavLink>
          </div>
        </section>

        <section className="content2-table">
          <div className="steps">
            <div className="step active">
              <div className="circle">1</div>
              <div className="label">Điền thông tin</div>
            </div>
            <div className="step">
              <div className="circle">2</div>
              <div className="label">Chọn bàn</div>
            </div>
            <div className="step">
              <div className="circle">3</div>
              <div className="label">Chọn món</div>
            </div>
            <div className="step">
              <div className="circle">4</div>
              <div className="label">Thanh toán</div>
            </div>
            <div className="step">
              <div className="circle">5</div>
              <div className="label">Xác nhận</div>
            </div>
          </div>
        </section>

        <section className="content3-table">
          <div className="reservation-container">
            <div className="reservation-image">
              <img
                src="https://chefjob.vn/wp-content/uploads/2017/12/set-up-nha-hang-la-gi.jpg"
                alt="Đặt chỗ"
              />
            </div>

            <div className="reservation-form">
              <h4 className="subtitle">Đặt chỗ</h4>
              <h2 className="title">Điền thông tin khách hàng</h2>

              <form onSubmit={handleSubmit}>
                <div className="row">
                  <input
                    type="text"
                    name="name"
                    placeholder="Họ và tên bạn"
                    value={formData.name}
                    onChange={handleChange}
                  />
                  <input
                    type="email"
                    name="email"
                    placeholder="Email của bạn"
                    value={formData.email}
                    onChange={handleChange}
                  />
                </div>
                <div className="row">
                  <input
                    type="datetime-local"
                    name="date"
                    value={formData.date}
                    onChange={handleChange}
                    min={minDateTime}
                  />
                  <input
                    type="number"
                    name="numberOfPeople"
                    placeholder="Số người ăn"
                    value={formData.numberOfPeople}
                    onChange={handleChange}
                    min={1}
                  />
                </div>
                <input
                  type="text"
                  name="phone"
                  placeholder="Số điện thoại"
                  value={formData.phone}
                  onChange={handleChange}
                />
                <textarea
                  name="note"
                  placeholder="Ghi chú thêm"
                  value={formData.note}
                  onChange={handleChange}
                ></textarea>
                <button type="submit" className="next-button">
                  TIẾP THEO
                </button>
              </form>
            </div>
          </div>
        </section>
      </div>

      <Modal
        isOpen={showLoginModal}
        className="custom-modal"
        overlayClassName="custom-overlay"
        ariaHideApp={false}
        shouldCloseOnOverlayClick={false}
      >
        <h2>Thông báo</h2>
        <p>
          Bạn cần đăng nhập để tiếp tục. Bạn có muốn chuyển đến trang đăng nhập
          không?
        </p>
        <div className="modal-buttons">
          <button className="cancel-button" onClick={handleCancelLogin}>
            HỦY
          </button>
          <button className="confirm-button" onClick={handleConfirmLogin}>
            ĐI ĐẾN ĐĂNG NHẬP
          </button>
        </div>
      </Modal>
    </>
  );
}

export default Table;
