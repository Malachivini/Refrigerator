import java.util.List;

public class Recipe {
    private String name;
    private List<String> ingredients;

    public Recipe(String name, List<String> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}
