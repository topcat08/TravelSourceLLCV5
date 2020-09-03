package screen;

// a simple object that holds data about a particular wine
public class Keywords {
    private String category_title;
    private double frequency_1;
    private double freqeuncy_2;
    private double frequency_3;
    private double frequency_4;

    private String keyword_1;
    private String keyword_2;
    private String keyword_3;
    private String keyword_4;

    private double averageFrequency = 0.0;
    private double totalFrequency = 0.0;
    private double percentFrequency = averageFrequency * 100 / totalFrequency;
    private String color;

    public String getCategory_title() {
        return category_title;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    public double getFrequency_1() {
        return frequency_1;
    }

    public void setFrequency_1(double frequency_1) {
        this.frequency_1 = frequency_1;
    }

    public double getFreqeuncy_2() {
        return freqeuncy_2;
    }

    public void setFreqeuncy_2(double freqeuncy_2) {
        this.freqeuncy_2 = freqeuncy_2;
    }

    public double getFrequency_3() {
        return frequency_3;
    }

    public void setFrequency_3(double frequency_3) {
        this.frequency_3 = frequency_3;
    }

    public double getFrequency_4() {
        return frequency_4;
    }

    public void setFrequency_4(double frequency_4) {
        this.frequency_4 = frequency_4;
    }

    public String getKeyword_1() {
        return keyword_1;
    }

    public void setKeyword_1(String keyword_1) {
        this.keyword_1 = keyword_1;
    }

    public String getKeyword_2() {
        return keyword_2;
    }

    public void setKeyword_2(String keyword_2) {
        this.keyword_2 = keyword_2;
    }

    public String getKeyword_3() {
        return keyword_3;
    }

    public void setKeyword_3(String keyword_3) {
        this.keyword_3 = keyword_3;
    }

    public String getKeyword_4() {
        return keyword_4;
    }

    public void setKeyword_4(String keyword_4) {
        this.keyword_4 = keyword_4;
    }
}
