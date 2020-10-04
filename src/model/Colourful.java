package screen;

public class Colourful {
    public double getTotalSumPerColor() {
        return totalSumPerColor;
    }

    public void setTotalSumPerColor(double totalSumPerColor) {
        this.totalSumPerColor = totalSumPerColor;
    }

    public double totalSumPerColor=0.0;
    public String color;
    public String hex;
    public String keyword;
    private String categoryCondition;


    public String getCategoryCondition() {
        return categoryCondition;
    }

    public void setCategoryCondition(String categoryCondition) {
        this.categoryCondition=categoryCondition;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String categories="";
    public String textHex;
    public int idKeywordTable;
    public String matctchType;
}
