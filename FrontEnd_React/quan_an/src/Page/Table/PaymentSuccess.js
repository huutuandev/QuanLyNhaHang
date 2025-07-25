import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";

function PaymentSuccess() {
    const navigate = useNavigate();
    const location = useLocation();
    useEffect(() => {
        const query = new URLSearchParams(location.search);
        const resultCode = query.get("resultCode");
        const amount = parseFloat(query.get("amount") || "0");

        const token = localStorage.getItem("token");
        const reservationId = localStorage.getItem("reservationId");
        const totalAmount = parseFloat(localStorage.getItem("totalAmount") || "0");
        const paidAmount = parseFloat(localStorage.getItem("paidAmount") || "0");

        if (resultCode === "0" && reservationId && token) {
            const createBill = async () => {
                try {
                    const payload = {
                        reservationId: parseInt(reservationId),
                        totalAmount,
                        paidAmount,
                        paymentMethod: "MoMo"
                    };
                    const res = await axios.post("/api/bills", payload, {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            "Content-Type": "application/json"
                        }
                    });
                    if (res.status === 200 || res.status === 201) {
                        alert("Thanh toán và tạo hóa đơn thành công!");
                        // 🧹 Xoá dữ liệu đã lưu
                        localStorage.removeItem("reservationId");
                        localStorage.removeItem("totalAmount");
                        localStorage.removeItem("paidAmount");

                        navigate("/"); // hoặc navigate("/") tùy bạn
                    } else {
                        alert("Tạo hóa đơn thất bại.");
                        navigate("/");
                    }

                } catch (error) {
                    console.error("❌ Lỗi khi tạo hóa đơn:", error);
                    alert("Đã xảy ra lỗi khi tạo hóa đơn.");
                    navigate("/");
                }
            };
            createBill();
        }
        else {
            alert("Thanh toán thất bại hoặc thiếu thông tin.");
            navigate("/");
        }
    }, [location, navigate]);
    return <p>Đang xử lý thanh toán, vui lòng chờ...</p>;
}
export default PaymentSuccess;