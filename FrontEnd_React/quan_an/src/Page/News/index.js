import { NavLink } from "react-router-dom";
import "./News.scss";
import { useEffect, useState } from "react";

function News() {
    const [Data, setData] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const size = 2; 

    useEffect(() => {
        fetch(`/api/posts?page=${page}&size=${size}`)
            .then((res) => res.json())
            .then((json) => {
                setData(json.content);
                setTotalPages(json.totalPages);
            });
    }, [page]);
    return (
        <div className="News">
            <section className="content1">
                <div className="content1__text">
                    <h1>TIN TỨC</h1>
                    <NavLink to="/">Trang Chủ /</NavLink>
                    <NavLink to="/News"> Tin Tức</NavLink>
                </div>
            </section>

            <section className="content2">
                <div className="content2__flex">
                    <div className="content2__left">
                        {Data.map((item) => (
                            <div className="box-vertical" key={item.id}>
                                <img src={item.imagUrl} alt={item.title} />
                                <p>{item.content}</p>
                                 <p><i>Tác giả: {item.authorName}</i></p>
                            </div>
                        ))}
                    </div>

                    <div className="content2__right">
                        <h2>Có thể bạn quan tâm</h2>
                        {Data.map((item) => (
                            <div className="box-horizontal" key={`right-${item.id}`}>
                                <p>{item.content}</p>
                                <img src={item.imagUrl} alt={item.title} />
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            <div className="pagination-circle">
                {[...Array(totalPages)].map((_, index) => (
                    <button
                        key={index}
                        onClick={() => setPage(index)}
                        className={index === page ? "active" : ""}
                    >
                        {index + 1}
                    </button>
                ))}
            </div>
        </div>
    );
}

export default News;
