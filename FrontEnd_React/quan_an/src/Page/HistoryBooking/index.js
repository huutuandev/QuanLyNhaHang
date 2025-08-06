import { useEffect, useState } from "react";
import axios from "axios";
import "./HistoryBooking.scss";
import Modal from "react-modal";
function ReservationHistory() {
  const [reservations, setReservations] = useState([]);
  const [bills, setBills] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const size = 5;

  const [showCancelModal, setShowCancelModal] = useState(false);
  const [selectedReservationId, setSelectedReservationId] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Bạn chưa đăng nhập");
      return;
    }

    axios
      .get(`/api/reservations/my?page=${page}&size=${size}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setReservations(res.data.content);
        setTotalPages(res.data.totalPages);

        return axios.get("/api/bills", {
          headers: { Authorization: `Bearer ${token}` },
        });
      })
      .then((res) => {
        setBills(res.data);
      })
      .catch((err) => console.error("Lỗi khi tải dữ liệu:", err));
  }, [page]);


  const handleCancel = (id) => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Bạn chưa đăng nhập");
      return;
    }

    axios
      .put(`/api/reservations/${id}`, null, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(() => {
        alert("Đã huỷ đặt bàn");
        setReservations((prev) => prev.filter((r) => r.id !== id));
      })
      .catch((err) => {
        console.error("Lỗi huỷ đặt bàn:", err);
        alert("Huỷ thất bại");
      });
  };

  const getTotalAmountByReservation = (reservationId) => {
    const bill = bills.find((b) => b.reservationId === reservationId);
    return bill?.totalAmount || 0;
  };
  const handleConfirmCancel = () => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Bạn chưa đăng nhập");
      return;
    }
    axios
      .put(`/api/reservations/${selectedReservationId}`, null, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(() => {
        alert("Đã huỷ đặt bàn");
        setReservations((prev) =>
          prev.filter((r) => r.id !== selectedReservationId)
        );
      })
      .catch((err) => {
        console.error("Lỗi huỷ đặt bàn:", err);
        alert("Huỷ thất bại");
      })
      .finally(() => {
        setShowCancelModal(false);
        setSelectedReservationId(null);
      });
  };

  const handleCancelModal = () => {
    setShowCancelModal(false);
    setSelectedReservationId(null);
  }

  const handlePay = async (reservationId) => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Bạn chưa đăng nhập");
      return;
    }

    const bill = bills.find((b) => b.reservationId === reservationId);
    if (!bill) {
      alert("Không tìm thấy hóa đơn cho đơn đặt bàn này.");
      return;
    }

    const paidAmount = bill.paidAmount;

    try {
      const res = await axios.post(
        "/api/momo/create-payment",
        {
          amount: paidAmount,
          orderInfo: `Thanh toán đơn ${reservationId}`
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          responseType:"text",
        }
      );

      const payUrl = res.data;
    if (payUrl && payUrl.startsWith("https://")) {
      window.location.href = payUrl;
    } else {
      alert("Không nhận được link thanh toán hợp lệ.");
    }
    } catch (error) {
      console.error("Lỗi khi tạo thanh toán:", error);
      alert("Thanh toán thất bại.");
    }
  };


  return (
    <>
      <div className="reservation-history">
        {reservations.length === 0 ? (
          <p>Không có đặt bàn nào.</p>
        ) : (
          reservations.map((res) => (
            <div key={res.id} className="reservation-card">
              <div className="reservation-header">
                <strong>{res.reservationistName || "Khách đặt bàn"}</strong>
                <span className="status-label">
                  {res.status === "Cancelled"
                    ? "đã huỷ đơn "
                    : res.isPaid === false
                      ? "Chờ thanh toán cọc"
                      : "Đã thanh toán cọc"}
                </span>
              </div>
              <div className="reservation-info">
                <p>
                  <strong>Số điện thoại:</strong> {res.reservationistPhone || "Không có"}
                </p>
                <p>
                  <strong>Ngày đặt:</strong> {res.reservationDate} {res.reservationTime}
                </p>
                <p>
                  <strong>Số người:</strong> {res.numberOfGuests} &nbsp;&nbsp;&nbsp;&nbsp;
                  <strong>Số bàn:</strong> {res.tableNumber || "Chưa có"}
                </p>
                <p>
                  <strong>Số tiền thanh toán:</strong>{" "}
                  {getTotalAmountByReservation(res.id)
                    ? getTotalAmountByReservation(res.id).toLocaleString("vi-VN") + " VND"
                    : "Không có"}
                </p>
              </div>

              <div className="reservation-actions">
                <button className="btn btn-outline-success">XEM CHI TIẾT</button>
                {res.status !== "Cancelled" && res.isPaid == false && (
                  <button className="btn btn-warning" onClick={()=>handlePay(res.id)}>THANH TOÁN</button>
                )}
                {res.status !== "Cancelled" && (
                  <button className="btn btn-danger" onClick={() => {
                    setSelectedReservationId(res.id);
                    setShowCancelModal(true);
                  }}>
                    HỦY ĐẶT BÀN
                  </button>
                )}
              </div>
            </div>
          ))
        )}

        <div className="custom-pagination">
          {[...Array(Number(totalPages)).keys()].map((i) => (
            <button
              key={i}
              onClick={() => setPage(i)}
              className={`page-circle ${i === page ? "active" : ""}`}
            >
              {i + 1}
            </button>
          ))}
        </div>
      </div>
      <Modal
        isOpen={showCancelModal}
        className="custom-modal"
        overlayClassName="custom-overlay"
        ariaHideApp={false}
        shouldCloseOnOverlayClick={false}
      >
        <h2>Xác nhận huỷ đơn</h2>
        <p>Nếu huỷ đặt bàn, bạn sẽ mất khoản tiền cọc đã thanh toán. Bạn có chắc chắn muốn huỷ không?</p>
        <div className="modal-buttons">
          <button className="cancel-button" onClick={handleCancelModal}>
            KHÔNG HUỶ
          </button>
          <button className="confirm-button" onClick={handleConfirmCancel}>
            ĐỒNG Ý HUỶ
          </button>
        </div>
      </Modal>
    </>
  );
}

export default ReservationHistory;
