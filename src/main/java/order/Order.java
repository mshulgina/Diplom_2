package order;

public class Order {

    public static Ingredients getIngredients() {
        return new Ingredients(new String[]{"Ингредиент1", "Ингредиент2"});
    }

    public static Ingredients getIngredientsEmpty() {
        return new Ingredients(new String[]{});
    }

    public static Ingredients getIngredientsIncorrect() {
        return new Ingredients(new String[]{"Неверный ингредиент1", "Неверный ингредиент2"});
    }
}