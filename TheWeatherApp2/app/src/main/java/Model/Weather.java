package Model;


public class Weather {
    public Timeframe timeframe = new Timeframe();
    public Code code = new Code();
    public Location location;
    public String iconData;
    public CurrentCondition currentCondition = new CurrentCondition();
    public Temperature temperature = new Temperature();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Clouds clouds = new Clouds();
}
