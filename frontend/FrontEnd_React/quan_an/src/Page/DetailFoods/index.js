import axios from "axios";
import "./DetailFoods.scss";
import { useEffect, useState } from "react";
import { useNavigate, NavLink } from "react-router-dom";

function DetailFoods() {
  const [foodDetail, setFoodDetail] = useState(null);
  const navigate = useNavigate();
  const [selectedRating, setSelectedRating] = useState(4);

  const [name, setName] = useState("");
  const [comment, setComment] = useState("");
  const [reviews, setReviews] = useState([]);

  useEffect(() => {
    const id = localStorage.getItem("selectedFoodId");
    if (id) {
      fetch(`/api/categories/foods/${id}`)
        .then((res) => res.json())
        .then((data) => {
          setFoodDetail(data);
          setReviews(data.reviews || []);
        })
        .catch((err) => console.error("Lỗi khi lấy chi tiết món ăn:", err));
    }
  }, []);

  if (!foodDetail) return <p className="loading">Đang tải...</p>;

  const { foodDTO, relatedFoods } = foodDetail;

  const handelSubmitReviews = () => {
    if (!name.trim() || !comment.trim()) {
      alert("Vui lòng nhập đầy đủ và bình luận");
      return;
    }

    const newReview = {
      foodId: foodDTO.id,
      comment,
      rating: selectedRating
    };


    axios.post(`/api/review`, newReview, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
    })
      .then(() => {
        return fetch(`/api/categories/foods/${foodDTO.id}`)
          .then(res => res.json())
          .then(data => {
            setReviews(data.reviews || []);
            setName("");
            setComment("");
            setSelectedRating(4);
          });
      })
      .catch((err) => {
        console.error("lỗi khi gửi đánh giá:", err);
        alert("gửi đánh giá thất bại");
      })
  }


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
            <img
              src={foodDTO.imageUrl}
              alt={foodDTO.name}
              className="main-image"
            />
            <div className="info-text">
              <h1>{foodDTO.name}</h1>
              <p className="description">{foodDTO.description}</p>
              <p className="price">
                Giá: {foodDTO.price.toLocaleString()} đ
              </p>
              <button
                className="btn-book"
                onClick={() => navigate("/Table")}
              >
                ĐẶT BÀN NGAY
              </button>
            </div>
          </div>

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

          <div className="review-container">
            <div className="review-left">
              <h3>ĐÁNH GIÁ MÓN ĂN NÀY</h3>
              {reviews.map((review, index) => (
                <div className="review-item" key={index}>
                  <img src="/avatar.png" alt="avatar" className="avatar" />
                  <div>
                    <p className="username">{review.username}</p>
                    <p className="comment">{review.comment}</p>
                    <div className="rating-date">
                      <span className="stars">
                        {"★".repeat(review.rating)}{"☆".repeat(5 - review.rating)}
                      </span>
                      <span className="date">{review.date}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            <div className="review-right">
              <h3>ĐÁNH GIÁ NHÀ HÀNG NÀY</h3>
              <div className="star-input">
                {[1, 2, 3, 4, 5].map((star) => (
                  <span
                    key={star}
                    className={`star ${star <= selectedRating ? "active" : ""}`}
                    onClick={() => setSelectedRating(star)}
                  >
                    ★
                  </span>
                ))}
              </div>

              <input type="text" placeholder="Tên" value={name} onChange={(e) => setName(e.target.value)} />
              <textarea placeholder="Bình luận đánh giá" value={comment} onChange={(e) => setComment(e.target.value)}></textarea>
              <button onClick={handelSubmitReviews}>GỬI ĐÁNH GIÁ</button>
            </div>
          </div>

        </div>
      </div>
    </>
  );
}

export default DetailFoods;
