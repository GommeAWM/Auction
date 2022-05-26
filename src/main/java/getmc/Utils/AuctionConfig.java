package getmc.Utils;

import cn.nukkit.utils.Config;
import getmc.Auction;

import java.io.File;

public class AuctionConfig {

    private Auction auction;
    private File file;
    private Config config;

    public AuctionConfig(Auction auction){
        this.auction = auction;
        this.file = new File(auction.getDataFolder(), "/config.yml");
        this.config = new Config(this.file, Config.YAML);
    }

    public void createDefaultConfig(){
        this.addDefault("options.messages.prefix", "§c[§fAuction§c] §7");
        this.addDefault("options.messages.isNotAPlayer", "§cYou are not a player!");
        this.addDefault("options.messages.price", "§cThe price need be more than 0");
        this.addDefault("options.messages.value", "$");
        this.addDefault("options.messages.timevalue", " Hours");
        this.addDefault("options.messages.permission", "§cYou don't have Permission");
        this.addDefault("options.messages.take", "§cTake Item in your hand");
        this.addDefault("options.messages.maximal", "§cYou reached the maximal of the item in Auction - Wait until the end timer and take it from ChestMenu");
        this.addDefault("options.messages.false", "§cSomething went false, try");
        this.addDefault("options.messages.buy", "§aYou successfully bought an item");
        this.addDefault("options.messages.get", "§aYou successfully take your Item from Storage");
        this.addDefault("options.messages.sell", "§aYou successfully put your Item to Auction");
        this.addDefault("options.messages.usg", "§cUsage: /ah sell <price>");
        this.addDefault("options.messages.ownbuy", "§aYour Item Has Been Successfully Sold");
        this.addDefault("options.messages.money", "§cThe price is higher than your money balance");
        this.addDefault("options.messages.cantbuy", "§6You can't buy your Item!!");
        this.addDefault("options.auction.title", "Auction");
        this.addDefault("options.auction.item.storage", "Storage");
        this.addDefault("options.auction.item.info", "Information");
        this.addDefault("options.auction.item.next", "Next");
        this.addDefault("options.auction.item.back", "Back");
        this.addDefault("options.auction.item.backstorage", "return");
        this.addDefault("options.auction.item.text-price", "§cPrice: §b");
        this.addDefault("options.auction.item.text-owner", "§fOwner: §b");
        this.addDefault("options.auction.item.text-until", "§cEnd: §b");
        this.addDefault("options.auction.item.text-date", "§fDate: §b");
        this.addDefault("options.auction.item.text-hash", "§cHash: §b");
        this.addDefault("options.auction.item.text-information", "Info - Edit in Config");
        this.addDefault("options.auction.maxitems", 5);
        this.addDefault("options.auction.timerhours", 5);
        this.addDefault("command.description", "Description - Check Config");
        this.addDefault("command.usageMessage", "§c/ah sell <price>");
    }

    public String prefix() {
        return this.config.getString("options.messages.prefix");
    }

    public String price() {
        return this.config.getString("options.messages.price");
    }

    public String take() {
        return this.config.getString("options.messages.take");
    }

    public String value() {
        return this.config.getString("options.messages.value");
    }


    public String maximal() {
        return this.config.getString("options.messages.maximal");
    }

    public String timevalue() {
        return this.config.getString("options.messages.timevalue");
    }


    public String sfalse() {
        return this.config.getString("options.messages.false");
    }


    public String buy() {
        return this.config.getString("options.messages.buy");
    }

    public String get() {
        return this.config.getString("options.messages.get");
    }

    public String sell(){
        return this.config.getString("options.messages.sell");
    }

    public String usg(){ return this.config.getString("options.messages.usg"); }

    public String ownbuy(){ return this.config.getString("options.messages.ownbuy"); }

    public String money(){ return this.config.getString("options.messages.money"); }

    public String title(){ return this.config.getString("options.auction.title"); }

    public String Storage(){ return this.config.getString("options.auction.item.storage"); }

    public String Info(){ return this.config.getString("options.auction.item.info"); }

    public String Next(){ return this.config.getString("options.auction.item.next"); }

    public String Back(){ return this.config.getString("options.auction.item.back"); }

    public String backstorage(){ return this.config.getString("options.auction.item.backstorage"); }

    public String textprice(){ return this.config.getString("options.auction.item.text-price"); }

    public String textowner(){ return this.config.getString("options.auction.item.text-owner"); }

    public String textuntil(){ return this.config.getString("options.auction.item.text-until"); }

    public String textdate(){ return this.config.getString("options.auction.item.text-date"); }

    public String texthash(){ return this.config.getString("options.auction.item.text-hash"); }

    public String textinfo(){ return this.config.getString("options.auction.item.text-information"); }

    public Integer max(){ return this.config.getInt("options.auction.maxitems"); }

    public Integer timer(){ return this.config.getInt("options.auction.timerhours"); }

    public String permission(){ return this.config.getString("options.messages.permission"); }

    public String cantbuy(){ return this.config.getString("options.messages.cantbuy"); }

    public String commandDescription() {
        return this.config.getString("command.description");
    }

    public String usageMessage() {
        return this.config.getString("command.usageMessage");
    }

    public void addDefault(String path, Object object){
        if(!this.config.exists(path)){
            this.config.set(path, object);
            this.config.save(this.file);
        }
    }

}
