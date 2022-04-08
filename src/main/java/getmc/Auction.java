package getmc;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.Config;
import getmc.Command.AucCMD;
import getmc.Listener.InventoryTransactionListener;
import getmc.Utils.AuctionConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.File;
import java.util.*;

public class Auction extends PluginBase {

    private static Auction instance;

    public List<Integer> pages = new ArrayList<>();
    public Int2ObjectOpenHashMap<Item> chest;
    public Int2ObjectOpenHashMap<Item> chest1;
    public Int2ObjectOpenHashMap<Item> chest2;
    public Int2ObjectOpenHashMap<Item> chest3;
    public Int2ObjectOpenHashMap<Item> chest4;
    public Int2ObjectOpenHashMap<Item> chest5;
    public Int2ObjectOpenHashMap<Item> chest6;
    public Int2ObjectOpenHashMap<Item> chest7;
    public Int2ObjectOpenHashMap<Item> chest8;
    public Int2ObjectOpenHashMap<Item> chest9;
    public Int2ObjectOpenHashMap<Item> chest10;
    public Int2ObjectOpenHashMap<Item> chest11;
    public Int2ObjectOpenHashMap<Item> chest12;
    public Int2ObjectOpenHashMap<Item> chest13;
    public Int2ObjectOpenHashMap<Item> chest14;
    public Int2ObjectOpenHashMap<Item> chest15;
    public Int2ObjectOpenHashMap<Item> chest16;
    public Int2ObjectOpenHashMap<Item> chest17;
    public Int2ObjectOpenHashMap<Item> chest18;
    public Int2ObjectOpenHashMap<Item> chest19;
    public Int2ObjectOpenHashMap<Item> chest20;
//    public Int2ObjectOpenHashMap<Item> chest21;
//    public Int2ObjectOpenHashMap<Item> chest22;
//    public Int2ObjectOpenHashMap<Item> chest23;
//    public Int2ObjectOpenHashMap<Item> chest24;
//    public Int2ObjectOpenHashMap<Item> chest25;
//    public Int2ObjectOpenHashMap<Item> chest26;
//    public Int2ObjectOpenHashMap<Item> chest27;
//    public Int2ObjectOpenHashMap<Item> chest28;
//    public Int2ObjectOpenHashMap<Item> chest29;
//    public Int2ObjectOpenHashMap<Item> chest30;

    private static AuctionConfig auctionConfig;

//    public List<Item> chest1 = new ArrayList<>();

    public List<String> feedremove = new ArrayList<String>();
    public List<String> feedkt = new ArrayList<>();

    public HashMap<String, Integer> timerList1 = new HashMap();


    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getTimesForAuc();
        auctionConfig = new AuctionConfig(this);
        auctionConfig.createDefaultConfig();
        startSchedulerItemAuction();
        register();
        this.getLogger().info("§fEnable: §a§lAuction");
    }

    private void register(){
        SimpleCommandMap simpleCommandMap = getServer().getCommandMap();
//        simpleCommandMap.register("ah", new AucCMD());
        simpleCommandMap.register("help", new AucCMD("ah", Auction.getAuctionConfig().commandDescription(), Auction.getAuctionConfig().usageMessage()));

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new InventoryTransactionListener(), this);
    }

    public static AuctionConfig getAuctionConfig(){
        return auctionConfig;
    }


    private void getTimesForAuc(){

        Config timercfg = new Config(new File(Auction.getAuction().getDataFolder(), "/timer.yml"), Config.YAML);
        timercfg.reload();

        for(Map.Entry<String, Object> get2 : timercfg.getSections("Timer").getAll().entrySet()) {
            String get1 = get2.getKey();


            int timer = timercfg.getInt("Timer." + get1 + ".Count");


            timerList1.put(get1, timer);

        }

    }

    private void startSchedulerItemAuction(){

        Config timercfg = new Config(new File(Auction.getAuction().getDataFolder(), "/timer.yml"), Config.YAML);
        timercfg.reload();

        Config auccfg = new Config(new File(Auction.getAuction().getDataFolder(), "/auction.yml"), Config.YAML);
        auccfg.reload();

        try {

            instance.getServer().getScheduler().scheduleRepeatingTask(this.instance, new Runnable() {
                @Override
                public void run() {
                    Set<String> players = timerList1.keySet();
                    if (timerList1.size() > 0) {
                        Iterator<String> var3 = players.iterator();

                        while(var3.hasNext()) {
                            String p = var3.next();
                            int count = (Integer)timerList1.get(p);

                            if (count <= 0){

                                Config auccfg = new Config(new File(Auction.getAuction().getDataFolder(), "/auction.yml"), Config.YAML);
                                auccfg.reload();

                                Config playercfg = new Config(new File(Auction.getAuction().getDataFolder(), "/playercfg.yml"), Config.YAML);
                                playercfg.reload();

                                // get the Item

                                int price = auccfg.getInt("Auction." + p + ".Cost");
                                String item1 = auccfg.getString("Auction." + p + ".Items");
                                String owner = auccfg.getString("Auction." + p + ".Owner");
                                int id = auccfg.getInt("Auction." + p + ".Id");
                                int count1 = auccfg.getInt("Auction." + p + ".Count");
                                int damege = auccfg.getInt("Auction." + p + ".Damage");
                                String hashcode = auccfg.getString("Auction." + p + ".Hash");
                                String date = auccfg.getString("Auction." + p + ".Date");

                                // save to players config

                                String nowner = owner.toLowerCase();


                                playercfg.set("Inventory." + nowner + "." + p + ".Cost", 0);
                                playercfg.set("Inventory." + nowner + "." + p + ".Items", item1);
                                playercfg.set("Inventory." + nowner + "." + p + ".Owner", owner);
                                playercfg.set("Inventory." + nowner + "." + p + ".Count", count1);
                                playercfg.set("Inventory." + nowner + "." + p + ".Id", id);
                                playercfg.set("Inventory." + nowner + "." + p + ".Date", date);
                                playercfg.set("Inventory." + nowner + "." + p + ".Hash", hashcode);
                                playercfg.set("Inventory." + nowner + "." + p + ".Damage", damege);

                                for (String enchId : auccfg.getSection("Auction." + p + ".Enchants").getKeys(false)) {
                                    int id1 = Integer.parseInt(enchId);
                                    int lvl = auccfg.getInt("Auction." + p + ".Enchants." + enchId);
                                    playercfg.set("Inventory." + owner + "."  + p + ".Enchants." + id1,
                                            lvl);
                                }

                                playercfg.save();

                                timercfg.set("Timer." + p, null);
                                timercfg.save();

                                auccfg.set("Auction." + hashcode, null);
                                auccfg.save();

                                feedremove.add(p);
                                feedkt.remove(p);

                            } else {

                                int ncount = count - 1;

                                timercfg.set("Timer." + p + ".Count", ncount);
                                timercfg.save();

                                timerList1.replace(p, count - 1);
                            }

                        }
                    }

                    try {
                        if (feedremove.size() > 0){
                            for (int d = 0; d <= feedremove.size(); d++){
                                String player = feedremove.get(d);
                                timerList1.remove(player);
                                feedremove.remove(d);
                            }
                        }
                    } catch (IndexOutOfBoundsException e){
                    }

                }
            }, 20 * 3600);
        } catch (ConcurrentModificationException exception){
        }

    }

    @Override
    public void onDisable() {
        this.getLogger().info("§fDisable: §c§lAuction");
    }

    public static Auction getAuction(){
        return instance;
    }

}
