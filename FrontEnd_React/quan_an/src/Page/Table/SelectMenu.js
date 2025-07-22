import { NavLink, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import "./SelectMenu.scss";

function SelectMenu() {
    const [reservationData, setReservationData] = useState(null);
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState("");
    const [selectedFoods, setSelectedFoods] = useState({});
    const navigate = useNavigate();

    useEffect(() => {
        const data = localStorage.getItem("reservation");
        if (data) {
            setReservationData(JSON.parse(data));
        }

        const fetchData = async () => {
            try {
                const response = await axios.get("/api/categories");
                setCategories(response.data);
                if (response.data.length > 0) {
                    setSelectedCategory(response.data[0].name);
                }
            } catch (error) {
                console.error("Lỗi lấy danh mục:", error);
            }
        };

        fetchData();
    }, []);

    const handleIncrement = (food) => {
        setSelectedFoods((prev) => ({
            ...prev,
            [food.id]: {
                ...food,
                quantity: (prev[food.id]?.quantity || 0) + 1,
            },
        }));
    };

    const handleDecrement = (food) => {
        setSelectedFoods((prev) => {
            const current = prev[food.id]?.quantity || 0;
            if (current <= 1) {
                const updated = { ...prev };
                delete updated[food.id];
                return updated;
            }
            return {
                ...prev,
                [food.id]: { ...food, quantity: current - 1 },
            };
        });
    };
    const selectedFoodsList = Object.values(selectedFoods);

    const handleNextStep=()=>{
        const totalAmount=selectedFoodsList.reduce((sum,f)=>sum+f.quantity*f.price,0);
        const orderData={
            foods:selectedFoodsList,
            total:totalAmount,
        };
        localStorage.setItem("order",JSON.stringify(orderData));
        navigate("/pay");
    }

    if (!reservationData) return <p>Đang tải thông tin đặt bàn...</p>;

    const formattedDate = new Date(reservationData.date).toLocaleString("vi-VN", {
        dateStyle: "short",
        timeStyle: "short",
    });

    const activeCategory = categories.find((cat) => cat.name === selectedCategory);

    return (
        <div className="Main_SelectFood">
            <section className="content1-SelectFood">
                <div className="content1-SelectFood__text">
                    <h1>Đặt bàn online</h1>
                    <NavLink to="/">Trang Chủ /</NavLink>
                    <NavLink to="/Table"> Đặt Bàn /</NavLink>
                    <NavLink to="/SelectTable"> Chọn Bàn /</NavLink>
                    <NavLink to="/SelectMenu"> Chọn Món</NavLink>
                </div>
            </section>

            <section className="content2-SelectFood">
                <div className="select-menu">
                    <div className="left-panel">
                        <h2>Thông tin đặt bàn</h2>
                        <p><strong>Họ tên:</strong> <span>{reservationData.name}</span></p>
                        <p><strong>Email:</strong> <span>{reservationData.email}</span></p>
                        <p><strong>Số điện thoại:</strong> <span>{reservationData.phone}</span></p>
                        <p><strong>Thời gian đặt bàn:</strong> <span>{formattedDate}</span></p>
                        <p><strong>Số người:</strong> <span>{reservationData.numberOfPeople} người</span></p>
                        <p><strong>Ghi chú:</strong> <span>{reservationData.note || "Không có"}</span></p>
                    </div>

                    <div className="right-panel">
                        <div className="tabs">
                            {categories.map((category) => (
                                <button
                                    key={category.id}
                                    className={category.name === selectedCategory ? "active" : ""}
                                    onClick={() => setSelectedCategory(category.name)}
                                >
                                    {category.name}
                                </button>
                            ))}
                        </div>

                        <div className="food-list">
                            {activeCategory?.foods.content.map((food) => (
                                <div className="food-item" key={food.id}>
                                    <img src={food.imageUrl} alt={food.name} />
                                    <div className="info">
                                        <h4>{food.name}</h4>
                                        <p>{food.price.toLocaleString()} đ</p>
                                    </div>
                                    <div className="quantity-control">
                                        <button onClick={() => handleDecrement(food)}>-</button>
                                        <input
                                            type="text"
                                            readOnly
                                            value={selectedFoods[food.id]?.quantity || 0}
                                        />
                                        <button onClick={() => handleIncrement(food)}>+</button>
                                    </div>
                                </div>
                            ))}
                        </div>

                        <div className="selected-foods">
                            <h3>Danh sách món đã chọn</h3>
                            {selectedFoodsList.length === 0 ? (
                                <p className="empty">Chưa có món nào được chọn.</p>
                            ) : (
                                <>
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>Món ăn</th>
                                                <th>Số lượng</th>
                                                <th>Giá</th>
                                                <th>Thành tiền</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {selectedFoodsList.map((food) => (
                                                <tr key={food.id}>
                                                    <td>
                                                        <img src={food.imageUrl} alt={food.name} />
                                                        <span>{food.name}</span>
                                                    </td>
                                                    <td>{food.quantity}</td>
                                                    <td>{food.price.toLocaleString()} đ</td>
                                                    <td>{(food.quantity * food.price).toLocaleString()} đ</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                    <div className="total">
                                        <span>Tổng tiền:</span>
                                        <strong>
                                            {selectedFoodsList.reduce((sum, f) => sum + f.quantity * f.price, 0).toLocaleString()} đ
                                        </strong>
                                    </div>
                                    <div className="actions">
                                        <button className="back" onClick={() => navigate(-1)}>TRỞ LẠI</button>
                                        <button className="next" onClick={handleNextStep}>TIẾP THEO</button>
                                    </div>
                                </>
                            )}
                        </div>
                    </div>
                </div>
            </section>
        </div>
    );
}

export default SelectMenu;
