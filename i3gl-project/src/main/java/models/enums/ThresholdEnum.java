package models.enums;

public enum ThresholdEnum {
    RAIN("rain"),
    WIND("wind_speed_10m"),
    TEMPERATURE("temperature_2m");
    private final String name;
    ThresholdEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
