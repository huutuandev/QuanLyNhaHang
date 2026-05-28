function CategoryMenu({ categories, onSelect, selected }) {
    return (
        <>
            <div className="menu-left">
                <button
                    className={selected === "Tất cả" ? "active" : ""}
                    onClick={() => onSelect("Tất cả")}
                >
                    Xem tất cả
                </button>
                {categories.map((cat) => (
                    <button
                        key={cat.id}
                        className={selected === cat.name ? "active" : ""}
                        onClick={() => onSelect(cat.name)}
                    >
                        {cat.name}
                    </button>
                ))}
            </div>
        </>
    );
}

export default CategoryMenu;
