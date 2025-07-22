import "./DetailFoods.scss";
import { useEffect, useState } from "react";
import { useNavigate, NavLink } from "react-router-dom";

function DetailFoods() {
  const [foodDetail, setFoodDetail] = useState(null);
  const [currentReviewIndex, setCurrentReviewIndex] = useState(0); // 👈 state để chuyển comment
  const navigate = useNavigate();

  useEffect(() => {
    const id = localStorage.getItem("selectedFoodId");
    if (id) {
      fetch(`/api/categories/foods/${id}`)
        .then((res) => res.json())
        .then((data) => setFoodDetail(data))
        .catch((err) => console.error("Lỗi khi lấy chi tiết món ăn:", err));
    }
  }, []);

  useEffect(() => {
    if (foodDetail?.reviews?.length > 1) {
      const interval = setInterval(() => {
        setCurrentReviewIndex((prevIndex) =>
          (prevIndex + 1) % foodDetail.reviews.length
        );
      }, 5000);
      return () => clearInterval(interval);
    }
  }, [foodDetail]);

  if (!foodDetail) return <p className="loading">Đang tải...</p>;

  const { foodDTO, reviews, relatedFoods } = foodDetail;

  return (
    <>
    <div className="Detail">
      <section className="content1">
        <div className="content1__text">
          <h1>Chi tiết món ăn</h1>
          <NavLink to="/">Trang Chủ /</NavLink>
          <NavLink to="/DetailFoods"> Trang Chi tiết </NavLink>
        </div>
      </section>

      <div className="detail-page">
        <div className="main-info">
          <img src={foodDTO.imageUrl} alt={foodDTO.name} className="main-image" />
          <div className="info-text">
            <h1>{foodDTO.name}</h1>
            <p className="description">{foodDTO.description}</p>
            <p className="price">Giá: {foodDTO.price.toLocaleString()} đ</p>
            <button className="btn-book" onClick={() => navigate("/Table")}>ĐẶT BÀN NGAY</button>
          </div>
        </div>

        {reviews.length > 0 && (
          <div className="customer-review">
            <h2>Khách hàng đánh giá món ăn</h2>
            <p className="subtitle">Chia sẻ của khách hàng đã trải nghiệm món ăn tại nhà hàng</p>

            <div className="review-carousel">
              <div className="review-track" style={{ transform: `translateX(-${currentReviewIndex * 100}%)` }}>
                {reviews.map((review, index) => (
                  <div className="review-card" key={index}>
                    <img src="" alt="avatar" className="avatar" />
                    <p className="comment">{review.comment}</p>
                    <p className="username">{review.username}</p>
                    <p className="Sao"> Đánh giá {review.rating} ⭐</p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
        <div className="related-section">
          <h2>Món ăn liên quan</h2>
          <div className="related-list">
            {relatedFoods.map((food) => (
              <div
                key={food.id}
                className="related-card"
                onClick={() => {
                  localStorage.setItem("selectedFoodId", food.id);
                  window.location.reload();
                }}
              >
                <img src={food.imageUrl} alt={food.name} />
                <div className="card-text">
                  <h4>{food.name}</h4>
                  <p>{food.description}</p>
                  <span>{food.price.toLocaleString()} đ</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
    </>
  );
}

export default DetailFoods;
