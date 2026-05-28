import "./Home.scss"
import { LuChefHat } from "react-icons/lu";
import { TbToolsKitchen2 } from "react-icons/tb";
import { FaChair } from "react-icons/fa";
import { SlEarphonesAlt } from "react-icons/sl";
import { useState, useEffect } from "react";
import { NavLink } from "react-router-dom";
import { Link } from "react-router-dom";
import ChatRoom from "../ChatRoom";
import UserChat from "../UserChat";
function Home() {
    const [data, setData] = useState([]);
    const [newestFoods, setNewestFoods] = useState([]);
    const [topRatedFood, setTopRatedFood] = useState(null);
    const [featuredReviews, setFeaturedReviews] = useState([]);
    useEffect(() => {
        fetch("/api/home")
            .then((res) => res.json())
            .then((data) => {
                setNewestFoods(data.newestFoods || []);
                setTopRatedFood(data.topRatedFood || null);
                setFeaturedReviews(data.featuredReviews || []);
            })
            .catch((err) => {
                console.error("Lỗi khi fetch API:", err);
                setNewestFoods([]);
                setTopRatedFood(null);
                setFeaturedReviews([]);
            });
    }, []);
    const [currentReviewIndex, setCurrentReviewIndex] = useState(0);

    useEffect(() => {
        if (featuredReviews.length > 0) {
            const interval = setInterval(() => {
                setCurrentReviewIndex((prevIndex) =>
                    (prevIndex + 1) % featuredReviews.length
                );
            }, 10000);
            return () => clearInterval(interval);
        }
    }, [featuredReviews]);

    console.log(data);
    return (
        <>
            <div className="Main">
                <section className="content1">
                    <div className="content1__text">
                        <h1>NHỮNG MÓN ĂN NGON SẴN SÀN PHỤC VỤ THỰC KHÁCH</h1>
                        <p>
                            Khám phá hành trình ẩm thực châu Á đầy màu sắc. Với menu phong phú, từ những món ăn truyền thống đến những biến tấu mới lạ, chúng tôi mang đến cho thực khách những trải nghiệm ẩm thực độc đáo.
                        </p>
                        <Link to="/Table"><button className="btn-cta">ĐẶT BÀN NGAY</button></Link>
                    </div>
                    <div className="content1__image">
                        <img src="https://danviet.ex-cdn.com/files/f1/296231569849192448/2023/5/7/img-0557-1285-9882-1683425756832-16834257569881731036019.jpg"
                            alt="Mâm món ăn" />
                    </div>
                </section>


                <section className="content2">
                    <div className="content2__box">
                        <LuChefHat className="icon" />
                        <h3 className="title">
                            Đầu bếp nhiều năm kinh nghiệm
                        </h3>
                        <span className="text">
                            Đầu bếp chung tôi với 5 năm king nghiệm sẽ luôn mang đến cho quý khách những món ăn hảo hạng
                        </span>
                    </div>
                    <div className="content2__box">
                        <TbToolsKitchen2 className="icon" />
                        <h3 className="title">
                            Nguyên liệu tươi ngon nhất
                        </h3>
                        <span className="text">
                            Mỗi món ăn tại nhà hàng đều được chế biến từ những nguyên liệu tươi ngon nhất
                        </span>
                    </div>
                    <div className="content2__box">
                        <FaChair className="icon" />
                        <h3 className="title">
                            Đặt bàn dễ dàng nhanh chóng
                        </h3>
                        <span className="text">
                            Đặt bàn dễ dàng chỉ với vài cú click. Món ăn sẽ nhanh chóng được phục vụ khi khách hàng đến nơi
                        </span>
                    </div>
                    <div className="content2__box">
                        <SlEarphonesAlt className="icon" />
                        <h3 className="title">
                            Phục vụ tận tình xuyên suốt 24/7
                        </h3>
                        <span className="text">
                            Chúng tôi vẫn luôn sẳn sàng phục vụ quý khách 24/7.Liên hệ với chúng tôi ngay để được tư vấn dịch vụ nhà hàng và đặt bàn.
                        </span>
                    </div>
                </section>

                <section className="content3">
                    <div className="content3__MenuNew">
                        <div className="content3__after">
                            <span className="title_Home">Nhà Hàng THT</span>
                            <h3>Món Ăn Mới</h3>
                        </div>
                        <div className="content3__before">
                            {newestFoods.map((item) => (
                                <NavLink to="/DetailFoods"
                                    key={item.id}
                                    onClick={() => localStorage.setItem("selectedFoodId", item.id)}
                                >
                                    <div className="content3__box">
                                        <div className="image">
                                            <span className="tag_new">New</span>
                                            <img src={item.imageUrl} alt={item.name} />
                                        </div>
                                        <div className="text-price">
                                            <p>{item.name}</p>
                                            <span>{item.price.toLocaleString()} đ</span>
                                        </div>
                                    </div>
                                </NavLink>
                            ))}
                        </div>
                    </div>
                </section>
                {topRatedFood && (
                    <section className="content4">
                        <div className="content4__container">
                            <div className="content4__text">
                                <span className="label"> NHÀ HÀNG THT</span>
                                <h2>NHÀ HÀNG XỊN NHẤT ĐÀ NẴNG</h2>
                                <p>
                                    THT Nhà hàng  Đà Nẵng với phong cách Buffet sân vườn đầu tiên tại Đà Nẵng. Không gian lên đến hơn 600m2 với sức chứa gần 200 người và khu vui chơi riêng cho trẻ em.
                                </p>
                                <p>
                                    THT mang đến menu đa dạng, hấp dẫn, nguyên liệu tươi ngon kết hợp ẩm thực Á, Âu lẩu và nướng. Chắc chắn bạn sẽ hài lòng khi đến trải nghiệm tại đây.
                                </p>
                                <button className="btn-booking">ĐẶT BÀN NGAY <i className="fa fa-paper-plane"></i></button>
                            </div>
                            <div className="content4__image">
                                <img src={topRatedFood.imageUrl} alt={topRatedFood.name} />
                                {/* <div className="overlay-image">
                                    <img src="https://cdn.example.com/sample2.jpg" alt="overlay crab dish" />
                                </div> */}
                            </div>
                        </div>
                    </section>

                )}
                {featuredReviews.length > 0 && (
                    <section className="content5">
                        <h2>Cảm nhận của khách hàng</h2>
                        <div className="review-slider">
                            <div className="review-slide" key={featuredReviews[currentReviewIndex].id}>
                                <h4>{featuredReviews[currentReviewIndex].username}</h4>
                                <p>"{featuredReviews[currentReviewIndex].comment}"</p>
                                <span>Đánh giá: {featuredReviews[currentReviewIndex].rating} ⭐</span>
                            </div>
                        </div>
                    </section>
                )}
                <section>
                    {/* <ChatRoom/> */}
                    <UserChat/>
                </section>
            </div >
        </>
    )
}
export default Home;