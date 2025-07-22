function FoodList({ foods }) {
    const isGrouped =
        Array.isArray(foods) &&
        foods.length > 0 &&
        typeof foods[0].categoryName === "string" &&
        Array.isArray(foods[0].foods?.content);

    if (isGrouped) {
        return (
            <>
                {foods.map((group, index) => (
                    <div key={index}>
                        <h3 style={{ marginTop: "20px" }}>{group.categoryName}</h3>
                        <div className="food-list">
                            {group.foods.content.map((food) => (
                                <div className="food-item" key={food.id}>
                                    <img src={food.imageUrl} alt={food.name} />
                                    <h4>{food.name}</h4>
                                    <p>{food.description}</p>
                                    <p className="price">{food.price.toLocaleString()} đ</p>
                                </div>
                            ))}
                        </div>
                    </div>
                ))}
            </>
        );
    }

    return (
        <div className="food-list">
            {foods.map((food) => (
                <div className="food-item" key={food.id}>
                    <img src={food.imageUrl} alt={food.name} />
                    <h4>{food.name}</h4>
                    <p>{food.description}</p>
                    <p className="price">{food.price.toLocaleString()} đ</p>
                </div>
            ))}
        </div>
    );
}

export default FoodList;
