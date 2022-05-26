package getmc.Command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import com.nukkitx.fakeinventories.inventory.*;
import getmc.Auction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AucCMD extends Command {

    private Auction instance;
    public Config auccfg;
    public Config countcfg;
    public Config timercfg;

    public int a = 0;

    public AucCMD(String cmd, String description, String usg){
        super(cmd, description, usg);
    }


    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {

        if (commandSender instanceof Player){

            Player player = (Player) commandSender;

            if (!(player.hasPermission("reyd.auction"))){
                player.sendMessage(Auction.getAuctionConfig().permission());
                return true;
            }

            if (args.length == 0){
                openAction(player); // open Auction
            }

            if (args.length == 1){

                player.sendMessage(Auction.getAuctionConfig().usageMessage()); // Usage Message

            }

            /*

            Just add items in config

             */

            if (args.length == 2){

                try {

                    String name = player.getName().toLowerCase();
                    String msg = args[0];

                    int cost = Integer.parseInt(args[1]);

                    if (msg.equals("sell")){

                        /*

                        Get Config

                         */

                        Config auccfg = new Config(new File(Auction.getAuction().getDataFolder(), "/auction.yml"), Config.YAML);
                        auccfg.reload();

                        Config countcfg = new Config(new File(Auction.getAuction().getDataFolder(), "/count.yml"), Config.YAML);
                        countcfg.reload();

                        Config timercfg = new Config(new File(Auction.getAuction().getDataFolder(), "/timer.yml"), Config.YAML);
                        timercfg.reload();

                        /*

                        Check for price [args{1}]

                         */

                        if (cost <= 0){
                            player.sendMessage(Auction.getAuctionConfig().prefix() + Auction.getAuctionConfig().price());
                            return true;
                        }

                        /*

                        Check Hand if that's not a AIR (nothing)

                         */

                        if (player.getInventory().getItemInHand() == null || player.getInventory().getItemInHand() == Item.get(Item.AIR) || player.getInventory().getItemInHand().getId() == 0){
                            player.sendMessage(Auction.getAuctionConfig().prefix() + Auction.getAuctionConfig().take());

                            return true;
                        }

                        /*

                        Get important information of Item

                         */

                        Item item = player.getInventory().getItemInHand();
                        String item1 = player.getInventory().getItemInHand().getName();
                        int count = item.getCount();
                        Enchantment[] enchantment = item.getEnchantments();
                        int rep = item.getDamage();
                        int dam = item.getDamage();
                        byte[] nbt = item.getCompoundTag();
                        CompoundTag compoundTag = item.getNamedTag();
                        int ability = item.getEnchantAbility();
                        Enchantment idsenchants = item.getEnchantment(Enchantment.ID_DURABILITY);

                        String comp = String.valueOf(compoundTag);

                        if (countcfg.get(name) == null){
                            countcfg.set(name, 0);
                        }

                        /*`

                        Check if player have reached his MaxSellItems on Auction

                         */

                        int maxcountsellitems = countcfg.getInt(name);

                        if (maxcountsellitems == Auction.getAuctionConfig().max()){
                            player.sendMessage(Auction.getAuctionConfig().prefix() + Auction.getAuctionConfig().maximal());
                            return true;
                        } else {
                            maxcountsellitems++;
                            countcfg.set(name, maxcountsellitems);
                            countcfg.save();
                        }

                        String hashcode = generateRandomPassword(10); // generate HashCode
                        String date = getDate();

                        if (auccfg.get(hashcode) != null){

                            player.sendMessage(Auction.getAuctionConfig().prefix() + Auction.getAuctionConfig().sfalse());
                            return true;

                        } else {

                            /*

                            Put important Information info Config

                             */

                            startItemCount(hashcode);

                            String uid = String.valueOf(player.getUniqueId());
                            int time = Auction.getAuction().timerList1.get(hashcode);

                            auccfg.set("Auction." + hashcode + ".Cost", cost);
                            auccfg.set("Auction." + hashcode + ".Items", item1);
                            auccfg.set("Auction." + hashcode + ".Owner", name);
                            auccfg.set("Auction." + hashcode + ".Count", count);
                            auccfg.set("Auction." + hashcode + ".Id", item.getId());
                            auccfg.set("Auction." + hashcode + ".Date", date);
                            auccfg.set("Auction." + hashcode + ".Hash", hashcode);
                            auccfg.set("Auction." + hashcode + ".Damage", rep);
                            auccfg.set("Auction." + hashcode + ".Uid", uid);

                            timercfg.set("Timer." + hashcode + ".Count", time);
                            timercfg.save();

                            /*

                            Put Enchantments into Config

                             */

                            if (item.hasEnchantments()) {
                                for (int i1 = 0; i1 < item.getEnchantments().length; i1++) {
                                    Enchantment e1 = item.getEnchantments()[i1];
                                    auccfg.set("Auction." + hashcode + ".Enchants." + e1.getId(),
                                            e1.getLevel());
                                }
                            }

                            auccfg.save(); // IMPORTANT SAVE
                        }
                        player.getInventory().setItemInHand(Item.get(Item.AIR));

                        player.sendMessage(Auction.getAuctionConfig().prefix() + Auction.getAuctionConfig().sell());

                    }

                    if (msg.equals("info")){
                        player.sendMessage("§b[§aAuction§b] §fThe owner of this plugin is §aDaniel Reydovich §b(§eGommeAWM§b) §7// §cxxtdaniel");
                        return true;
                    }

                } catch (NumberFormatException e){
                    player.sendMessage(Auction.getAuctionConfig().prefix() + Auction.getAuctionConfig().usageMessage());
                }

            }

        }

        return true;
    }


    public void openAction(Player player){
        chestItems(player);


        if (Auction.getAuction().chest.isEmpty()){
            Item saveItems = Item.get(54).setCustomName(Auction.getAuctionConfig().Storage());
            Item infoItem = Item.get(388).setCustomName(Auction.getAuctionConfig().Info());
            Auction.getAuction().chest.put(45, infoItem);
            Auction.getAuction().chest.put(49,saveItems);
        }

        ChestFakeInventory ec = new DoubleChestFakeInventory();
        ec.setName(Auction.getAuctionConfig().title());
        ec.setTitle(Auction.getAuctionConfig().title());
        ec.setContents(Auction.getAuction().chest);

        player.addWindow(ec);
        ec.addListener(this::onSlotChange);
    }

    private void chestItems(Player player) {

        Config auccfg = new Config(new File(Auction.getAuction().getDataFolder(), "/auction.yml"), Config.YAML);
        auccfg.reload();

        Config timercfg = new Config(new File(Auction.getAuction().getDataFolder(), "/timer.yml"), Config.YAML);
        timercfg.reload();

        /*

        Just to keep Information of Item in this Area

         */

        Auction.getAuction().chest = new Int2ObjectOpenHashMap(); //Adds the chest items to this array
        Auction.getAuction().chest1 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest2 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest3 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest4 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest5 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest6 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest7 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest8 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest9 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest10 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest11 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest12 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest13 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest14 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest15 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest16 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest17 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest18 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest19 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array
        Auction.getAuction().chest20 = new Int2ObjectOpenHashMap<>(); //Adds the chest items to this array

        /*

        -1 is just for first Slot

         */

        int i = -1;
        int a = -1;
        int b = -1;
        int c = -1;
        int d = -1;
        int e = -1;
        int f = -1;
        int g = -1;
        int h = -1;
        int j = -1;
        int k = -1;
        int m = -1;
        int n = -1;
        int l = -1;
        int o = -1;
        int p = -1;
        int q = -1;
        int r = -1;
        int s = -1;
        int t = -1;
        int u = -1;

        /*

        Here we will get All Information from Config and put this into chest1, chest2...

         */

        for(Map.Entry<String, Object> get2 : auccfg.getSections("Auction").getAll().entrySet()){
            String get1 = get2.getKey();
            int price = auccfg.getInt("Auction." + get1 + ".Cost");
            String item1 = auccfg.getString("Auction." + get1 + ".Items");
            String owner = auccfg.getString("Auction." + get1 + ".Owner");
            int id = auccfg.getInt("Auction." + get1 + ".Id");
            int count1 = auccfg.getInt("Auction." + get1 + ".Count");
            int damege = auccfg.getInt("Auction." + get1 + ".Damage");
            String hashcode = auccfg.getString("Auction." + get1 + ".Hash");
            String date = auccfg.getString("Auction." + get1 + ".Date");

            int timer = timercfg.getInt("Timer." + get1 + ".Count");

            String textprice = Auction.getAuctionConfig().textprice();
            String value = Auction.getAuctionConfig().value();
            String timevalue = Auction.getAuctionConfig().timevalue();
            String textowner = Auction.getAuctionConfig().textowner();
            String textuntil = Auction.getAuctionConfig().textuntil();
            String textdate = Auction.getAuctionConfig().textdate();
            String texthash = Auction.getAuctionConfig().texthash();

            Item item2 = Item.get(id, damege, count1).setCustomName(item1 + "\n\n" + textprice + price + value + "\n" + textowner + owner + "\n" + textuntil + timer + timevalue + "\n" + texthash + hashcode + "\n" + textdate + date + "\n");
            item2.setDamage(damege);
            for (String enchId : auccfg.getSection("Auction." + get1 + ".Enchants").getKeys(false)) {
                int id1 = Integer.parseInt(enchId);
                int lvl = auccfg.getInt("Auction." + get1 + ".Enchants." + enchId);
                item2.addEnchantment(Enchantment.getEnchantment(id1).setLevel(lvl, false));
            }

            String storage = Auction.getAuctionConfig().Storage();
            String info = Auction.getAuctionConfig().Info();

            i++;

            if (i < 45){
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest.put(45, infoItem);
                Auction.getAuction().chest.put(49,saveItems);

                Auction.getAuction().chest.put(i, item2); //Sets the item in the chest

            }

            String next = Auction.getAuctionConfig().Next();
            String back = Auction.getAuctionConfig().Back();

            if (i > 44 && i < 90){
                Item itemNext1 = Item.get(339).setCustomName(next + "1");
                Item itemBack = Item.get(395).setCustomName(back + "0");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest1.put(45, infoItem);
                Auction.getAuction().chest.put(50,itemNext1);
                Auction.getAuction().chest1.put(48,itemBack);
                Auction.getAuction().chest1.put(49,saveItems);
                a++;
                Auction.getAuction().chest1.put(a, item2);
            }

            if (i > 89 && i < 135){
                Item itemNext1 = Item.get(339).setCustomName(next + "2");
                Item itemBack = Item.get(395).setCustomName(back + "1");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest2.put(45, infoItem);
                Auction.getAuction().chest1.put(50,itemNext1);
                Auction.getAuction().chest2.put(48,itemBack);
                Auction.getAuction().chest2.put(49,saveItems);
                b++;
                Auction.getAuction().chest2.put(b, item2);
            }

            if (i > 134 && i < 180){
                Item itemNext1 = Item.get(339).setCustomName(next + "3");
                Item itemBack = Item.get(395).setCustomName(back + "2");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest3.put(45, infoItem);
                Auction.getAuction().chest2.put(50,itemNext1);
                Auction.getAuction().chest3.put(48,itemBack);
                Auction.getAuction().chest3.put(49,saveItems);

                c++;
                Auction.getAuction().chest3.put(c, item2);
            }

            if (i > 179 && i < 225){
                Item itemNext1 = Item.get(339).setCustomName(next + "4");
                Item itemBack = Item.get(395).setCustomName(back + "3");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest4.put(45, infoItem);
                Auction.getAuction().chest3.put(50,itemNext1);
                Auction.getAuction().chest4.put(48,itemBack);
                Auction.getAuction().chest4.put(49,saveItems);

                d++;
                Auction.getAuction().chest4.put(d, item2);
            }

            if (i > 224 && i < 270){
                Item itemNext1 = Item.get(339).setCustomName(next + "5");
                Item itemBack = Item.get(395).setCustomName(back + "4");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest5.put(45, infoItem);
                Auction.getAuction().chest4.put(50,itemNext1);
                Auction.getAuction().chest5.put(48,itemBack);
                Auction.getAuction().chest5.put(49,saveItems);

                e++;
                Auction.getAuction().chest5.put(e, item2);
            }

            if (i > 269 && i < 315){
                Item itemNext1 = Item.get(339).setCustomName(next + "6");
                Item itemBack = Item.get(395).setCustomName(back + "5");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest6.put(45, infoItem);
                Auction.getAuction().chest5.put(50,itemNext1);
                Auction.getAuction().chest6.put(48,itemBack);
                Auction.getAuction().chest6.put(49,saveItems);

                f++;
                Auction.getAuction().chest6.put(f, item2);
            }

            if (i > 314 && i < 360){
                Item itemNext1 = Item.get(339).setCustomName(next + "7");
                Item itemBack = Item.get(395).setCustomName(back + "6");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest7.put(45, infoItem);
                Auction.getAuction().chest6.put(50,itemNext1);
                Auction.getAuction().chest7.put(48,itemBack);
                Auction.getAuction().chest7.put(49,saveItems);

                g++;
                Auction.getAuction().chest7.put(g, item2);
            }

            if (i > 359 && i < 405){
                Item itemNext1 = Item.get(339).setCustomName(next + "8");
                Item itemBack = Item.get(395).setCustomName(back + "7");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest8.put(45, infoItem);
                Auction.getAuction().chest7.put(50,itemNext1);
                Auction.getAuction().chest8.put(48,itemBack);
                Auction.getAuction().chest8.put(49,saveItems);

                h++;
                Auction.getAuction().chest8.put(h, item2);
            }

            if (i > 404 && i < 450){
                Item itemNext1 = Item.get(339).setCustomName(next + "9");
                Item itemBack = Item.get(395).setCustomName(back + "8");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest9.put(45, infoItem);
                Auction.getAuction().chest8.put(50,itemNext1);
                Auction.getAuction().chest9.put(48,itemBack);
                Auction.getAuction().chest9.put(49,saveItems);

                h++;
                Auction.getAuction().chest9.put(h, item2);
            }

            if (i > 449 && i < 495){
                Item itemNext1 = Item.get(339).setCustomName(next + "10");
                Item itemBack = Item.get(395).setCustomName(back + "9");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest10.put(45, infoItem);
                Auction.getAuction().chest9.put(50,itemNext1);
                Auction.getAuction().chest10.put(48,itemBack);
                Auction.getAuction().chest10.put(49,saveItems);

                j++;
                Auction.getAuction().chest10.put(j, item2);
            }

            if (i > 494 && i < 540){
                Item itemNext1 = Item.get(339).setCustomName(next + "11");
                Item itemBack = Item.get(395).setCustomName(back + "10");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest11.put(45, infoItem);
                Auction.getAuction().chest10.put(50,itemNext1);
                Auction.getAuction().chest11.put(48,itemBack);
                Auction.getAuction().chest11.put(49,saveItems);

                k++;
                Auction.getAuction().chest11.put(k, item2);
            }

            if (i > 539 && i < 585){
                Item itemNext1 = Item.get(339).setCustomName(next + "12");
                Item itemBack = Item.get(395).setCustomName(back + "11");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest12.put(45, infoItem);
                Auction.getAuction().chest11.put(50,itemNext1);
                Auction.getAuction().chest12.put(48,itemBack);
                Auction.getAuction().chest12.put(49,saveItems);

                m++;
                Auction.getAuction().chest12.put(m, item2);
            }

            if (i > 584 && i < 630){
                Item itemNext1 = Item.get(339).setCustomName(next + "13");
                Item itemBack = Item.get(395).setCustomName(back + "12");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest13.put(45, infoItem);
                Auction.getAuction().chest12.put(50,itemNext1);
                Auction.getAuction().chest13.put(48,itemBack);
                Auction.getAuction().chest13.put(49,saveItems);

                n++;
                Auction.getAuction().chest13.put(n, item2);
            }

            if (i > 584 && i < 630){
                Item itemNext1 = Item.get(339).setCustomName(next + "13");
                Item itemBack = Item.get(395).setCustomName(back + "12");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest13.put(45, infoItem);
                Auction.getAuction().chest12.put(50,itemNext1);
                Auction.getAuction().chest13.put(48,itemBack);
                Auction.getAuction().chest13.put(49,saveItems);

                l++;
                Auction.getAuction().chest13.put(l, item2);
            }

            if (i > 629 && i < 675){
                Item itemNext1 = Item.get(339).setCustomName(next + "14");
                Item itemBack = Item.get(395).setCustomName(back + "13");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest14.put(45, infoItem);
                Auction.getAuction().chest13.put(50,itemNext1);
                Auction.getAuction().chest14.put(48,itemBack);
                Auction.getAuction().chest14.put(49,saveItems);

                p++;
                Auction.getAuction().chest14.put(p, item2);
            }

            if (i > 674 && i < 720){
                Item itemNext1 = Item.get(339).setCustomName(next + "15");
                Item itemBack = Item.get(395).setCustomName(back + "14");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest15.put(45, infoItem);
                Auction.getAuction().chest14.put(50,itemNext1);
                Auction.getAuction().chest15.put(48,itemBack);
                Auction.getAuction().chest15.put(49,saveItems);

                o++;
                Auction.getAuction().chest15.put(o, item2);
            }

            if (i > 719 && i < 765){
                Item itemNext1 = Item.get(339).setCustomName(next + "16");
                Item itemBack = Item.get(395).setCustomName(back + "15");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest16.put(45, infoItem);
                Auction.getAuction().chest15.put(50,itemNext1);
                Auction.getAuction().chest16.put(48,itemBack);
                Auction.getAuction().chest16.put(49,saveItems);

                q++;
                Auction.getAuction().chest16.put(q, item2);
            }

            if (i > 764 && i < 810){
                Item itemNext1 = Item.get(339).setCustomName(next + "17");
                Item itemBack = Item.get(395).setCustomName(back + "16");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest17.put(45, infoItem);
                Auction.getAuction().chest16.put(50,itemNext1);
                Auction.getAuction().chest17.put(48,itemBack);
                Auction.getAuction().chest17.put(49,saveItems);

                r++;
                Auction.getAuction().chest17.put(r, item2);
            }

            if (i > 809 && i < 855){
                Item itemNext1 = Item.get(339).setCustomName(next + "18");
                Item itemBack = Item.get(395).setCustomName(back + "17");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest18.put(45, infoItem);
                Auction.getAuction().chest17.put(50,itemNext1);
                Auction.getAuction().chest18.put(48,itemBack);
                Auction.getAuction().chest18.put(49,saveItems);

                s++;
                Auction.getAuction().chest18.put(s, item2);
            }

            if (i > 854 && i < 900){
                Item itemNext1 = Item.get(339).setCustomName(next + "19");
                Item itemBack = Item.get(395).setCustomName(back + "18");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest19.put(45, infoItem);
                Auction.getAuction().chest18.put(50,itemNext1);
                Auction.getAuction().chest19.put(48,itemBack);
                Auction.getAuction().chest19.put(49,saveItems);

                t++;
                Auction.getAuction().chest19.put(t, item2);
            }

            if (i > 899 && i < 945){
                Item itemNext1 = Item.get(339).setCustomName(next + "20");
                Item itemBack = Item.get(395).setCustomName(back + "19");
                Item saveItems = Item.get(54).setCustomName(storage);
                Item infoItem = Item.get(388).setCustomName(info);
                Auction.getAuction().chest20.put(45, infoItem);
                Auction.getAuction().chest19.put(50,itemNext1);
                Auction.getAuction().chest20.put(48,itemBack);
                Auction.getAuction().chest20.put(49,saveItems);

                u++;
                Auction.getAuction().chest20.put(u, item2);
            }

            // r s t

        }
    }

    public static String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                +"lmnopqrstuvwxyz!@#$%&";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    public static String getDate() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return dateFormat.format(now);
    }

    public void onSlotChange(FakeSlotChangeEvent event){

        if (event.getInventory() instanceof DoubleChestFakeInventory){
            if (event.getInventory().getName().equals(Auction.getAuctionConfig().title())){
                event.setCancelled(true);
            }
        }

    }

    private void startItemCount(String p){

        if (!Auction.getAuction().timerList1.containsKey(p)){
        }

        Auction.getAuction().timerList1.put(p, Auction.getAuctionConfig().timer());
    }


}
