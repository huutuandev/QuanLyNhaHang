import { useEffect, useState } from "react";
import CategoryMenu from "./CategoryMenu";
import FoodList from "./FoodList";
import "./MenuList.scss";

function MenuList() {
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState("Tất cả");
    const [foods, setFoods] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const size = 4;

    useEffect(() => {
        fetch("/api/categories")
            .then((res) => res.json())
            .then((json) => setCategories(json));
    }, []);

    useEffect(() => {
        setPage(0);
    }, [selectedCategory]);

    useEffect(() => {
        if (selectedCategory === "Tất cả") {

            Promise.all(
                categories.map((cat) =>
                    fetch(`/api/categories/${cat.id}?page=0&size=4`)
                        .then((res) => res.json())
                        .then((json) => ({
                            categoryName: cat.name,
                            foods: {
                                content: json.foods?.content || [],
                            },
                        }))
                )
            ).then((results) => {
                setFoods(results); // foods sẽ là mảng nhóm
                setTotalPages(1);  // không dùng phân trang
            });
        } else {
            // Tìm danh mục đang chọn
            const cat = categories.find((cat) => cat.name === selectedCategory);
            if (cat) {
                fetch(`/api/categories/${cat.id}?page=${page}&size=${size}`)
                    .then((res) => res.json())
                    .then((json) => {
                        setFoods(json.foods?.content || []);
                        const total = json.foods?.totalPages ?? Math.ceil((json.foods?.totalElements || 0) / size);
                        setTotalPages(total);
                    });
            }
        }
    }, [selectedCategory, categories, page]);

    return (
        <div className="MenuList">
            <CategoryMenu
                categories={categories}
                selected={selectedCategory}
                onSelect={setSelectedCategory}
            />
            <div className="content">
                <h2>{selectedCategory}</h2>
                <FoodList foods={foods} />
                {selectedCategory !== "Tất cả" && (
                    <div className="pagination">
                        {[...Array(totalPages)].map((_, i) => (
                            <button
                                key={i}
                                className={page === i ? "page-btn active" : "page-btn"}
                                onClick={() => setPage(i)}
                            >
                                {i + 1}
                            </button>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default MenuList;
