import { NavLink, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import "./Pay.scss";

function Pay() {
    const navigate = useNavigate();
    const [orderData, setOrderData] = useState(null);
    const [reservationData, setReservationData] = useState(null);

    useEffect(() => {
        const order = localStorage.getItem("order");
        const reservation = localStorage.getItem("reservation");

        if (order) setOrderData(JSON.parse(order));
        if (reservation) setReservationData(JSON.parse(reservation));
    }, []);

    if (!orderData || !reservationData) {
        return <p>Đang tải dữ liệu đơn hàng hoặc đặt bàn...</p>;
    }

    const { foods = [], total: subtotal = 0 } = orderData;
    const tax = subtotal * 0.1;
    const total = subtotal + tax;
    const deposit = total * 0.3;
    const remaining = total * 0.7;

    const handleConfirmPayment = async () => {
        const token = localStorage.getItem("token");
        if (!token) {
            alert("Bạn chưa đăng nhập.");
            navigate("/login");
            return;
        }

        const reservationId = localStorage.getItem("reservationId");

        if (!reservationId) {
            alert("Thiếu thông tin để thanh toán.");
            return;
        }

        try {
            const payload = {
                reservationId: Number(reservationId),
                totalAmount:parseFloat(total),
                paidAmount:parseFloat(deposit),
            };

            const response = await axios.post("/api/bills", payload, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (response.status === 200 || response.status === 201) {
                console.log("Id đặt bàn:", reservationId);
                const paymentRes = await axios.post("/api/momo/create-payment", {
                    amount: Math.round(deposit),
                    orderInfo: "Thanh toán tiền cọc đặt bàn"
                }, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                });
                const { data } = paymentRes;
                if (typeof data === "string" && data.startsWith("https://")) {
                    localStorage.setItem("totalAmount", Math.round(total));
                    localStorage.setItem("paidAmount", Math.round(deposit));

                    localStorage.removeItem("order");
                    // localStorage.removeItem("reservation");
                    window.location.href = data;
                }
                else {
                    alert("Không lấy được link thanh toán MOMO.");
                }
            } else {
                alert("Không lấy được link thanh toán MOMO.");
            }
        } catch (error) {
            console.error(" Lỗi chi tiết:", error);

            if (error.response) {
                console.error(" Server trả về lỗi:", error.response.data);
                console.error(" Status code:", error.response.status);

                alert(` Lỗi ${error.response.status}: ${error.response.data?.message || "Không rõ lỗi"}`);
            } else if (error.request) {
                console.error(" Không nhận được phản hồi từ server:", error.request);
                alert("Không có phản hồi từ máy chủ. Kiểm tra kết nối hoặc cấu hình API.");
            } else {
                console.error(" Lỗi thiết lập request:", error.message);
                alert("Đã xảy ra lỗi không xác định.");
            }

            if (error.response?.status === 401) {
                alert("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
                localStorage.removeItem("token");
                navigate("/login");
            }
        }
    };
    return (
        <div className="Main_pay">
            <section className="content1-pay">
                <div className="content1-pay__text">
                    <h1>Đặt bàn online</h1>
                    <NavLink to="/">Trang Chủ /</NavLink>
                    <NavLink to="/Table"> Đặt Bàn /</NavLink>
                    {/* <NavLink to="/SelectTable"> Chọn Bàn /</NavLink> */}
                    <NavLink to="/SelectMenu"> Chọn Món /</NavLink>
                    <NavLink to="/pay"> Thanh toán</NavLink>
                </div>
            </section>

            <section className="content2-pay">
                <div className="left-panel">
                    <div className="order-text">
                        <p>Đơn hàng ({foods.length} sản phẩm)</p>
                    </div>
                    <div className="scroll-container">
                        {foods.map((food) => (
                            <div className="selected-food" key={food.id}>
                                <div className="img_food">
                                    <img src={food.imageUrl} alt={food.name} />
                                </div>
                                <div className="information_food">
                                    <p>{food.name}</p>
                                    <span>{food.quantity} x {food.price.toLocaleString()} đ</span>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="right-panel">
                    <h2>🧾 Tóm tắt đơn hàng</h2>

                    <div className="input-group">
                        <input type="text" placeholder="Nhập mã giảm giá" />
                        <button>ÁP DỤNG</button>
                    </div>
                    <p style={{ color: "gray", fontSize: "13px" }}>
                        Không có mã giảm giá hiện tại.
                    </p>

                    <div className="info-line">
                        <span>Tạm tính:</span>
                        <span>{subtotal.toLocaleString()} đ</span>
                    </div>
                    <div className="info-line">
                        <span>Giảm giá:</span>
                        <span>0 đ</span>
                    </div>
                    <div className="info-line">
                        <span>Thuế (10%):</span>
                        <span>{tax.toLocaleString()} đ</span>
                    </div>

                    <div className="total-line">
                        <span>Tổng thanh toán: {total.toLocaleString()} đ</span>
                    </div>

                    <div className="payment-method">
                        <p><strong>Phương thức thanh toán</strong></p>
                        <p>Cọc (30%): {deposit.toLocaleString()} đ</p>
                        <p>Còn lại (70%): {remaining.toLocaleString()} đ</p>

                        <div className="radio-group">
                            <label>
                                <input type="radio" name="payment" defaultChecked /> Thanh toán bằng MOMO
                            </label>
                            <label>
                                <input type="radio" name="payment" /> Thanh toán bằng VNPay
                            </label>
                        </div>
                    </div>

                    <div className="action-buttons">
                        <button className="btn-back" onClick={() => navigate(-1)}>TRỞ LẠI</button>
                        <button className="btn-confirm" onClick={handleConfirmPayment}>XÁC NHẬN THANH TOÁN</button>
                    </div>
                </div>
            </section>
        </div>
    );
}

export default Pay;
