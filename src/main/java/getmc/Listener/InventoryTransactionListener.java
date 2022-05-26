package getmc.Listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Config;
import com.nukkitx.fakeinventories.inventory.*;
import getmc.Auction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryTransactionListener implements Listener {

    public int d;

    HashMap<Player, HashMap<Integer, Item>> outerMap = new HashMap<Player, HashMap<Integer,Item>>();
    HashMap<Integer, Item> innerMap = new HashMap<Integer, Item>();

    @EventHandler
    public void onChest(InventoryTransactionEvent event) {
        Player pl = event.getTransaction().getSource();


        InventoryTransaction trans = event.getTransaction();
        for (InventoryAction action : trans.getActions()) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction act = (SlotChangeAction) action;
                if (act.getInventory() instanceof DoubleChestFakeInventory) {

                    if (act.getInventory().getName().equals(Auction.getAuctionConfig().title())) {

                        Config auccfg = new Config(new File(Auction.getAuction().getDataFolder(), "/auction.yml"), Config.YAML);
                        auccfg.reload();

                        Config timercfg = new Config(new File(Auction.getAuction().getDataFolder(), "/timer.yml"), Config.YAML);
                        timercfg.reload();

                        Config countcfg = new Config(new File(Auction.getAuction().getDataFolder(), "/count.yml"), Config.YAML);
                        countcfg.reload();

                        interact(action.getSourceItem(), pl);

                        for (Map.Entry<String, Object> get2 : auccfg.getSections("Auction").getAll().entrySet()) {
                            String get1 = get2.getKey();
                            String hashcode = auccfg.getString("Auction." + get1 + ".Hash");
                            if (action.getSourceItem().getCustomName().contains(hashcode)) {

                                int price = auccfg.getInt("Auction." + get1 + ".Cost");
                                String owner = auccfg.getString("Auction." + get1 + ".Owner");
                                int id = auccfg.getInt("Auction." + get1 + ".Id");
                                int count1 = auccfg.getInt("Auction." + get1 + ".Count");
                                int damege = auccfg.getInt("Auction." + get1 + ".Damage");
                                String uid = auccfg.getString("Auction." + get1 + ".Uid");

                                double maincount = Auction.getAuction().getMoney(pl.getName());

                                if (maincount >= price) {

                                    if (pl.getName().toLowerCase().equals(owner)){

                                        pl.sendMessage(Auction.getAuctionConfig().cantbuy());

                                    } else {

                                        double resultprice = maincount - price;
                                        Auction.getAuction().setMoney(pl, resultprice);
                                        int now = (int) Auction.getAuction().getMoney(owner);
                                        UUID uidr = UUID.fromString(uid);
                                        Auction.getAuction().addMoney(uidr, price);

                                        Item item12 = Item.get(id, damege, count1);
                                        item12.setDamage(damege);
                                        for (String enchId : auccfg.getSection("Auction." + get1 + ".Enchants").getKeys(false)) {
                                            int id1 = Integer.parseInt(enchId);
                                            int lvl = auccfg.getInt("Auction." + get1 + ".Enchants." + enchId);
                                            item12.addEnchantment(Enchantment.getEnchantment(id1).setLevel(lvl, false));
                                        }

                                        int count = countcfg.getInt(owner);
                                        int result = count - 1;
                                        countcfg.set(owner, result);
                                        countcfg.save();

                                        if (Server.getInstance().getPlayer(owner) != null) {
                                            Server.getInstance().getPlayer(owner).sendMessage(Auction.getAuctionConfig().prefix() + Auction.getAuctionConfig().ownbuy());
                                        }
                                        pl.getInventory().addItem(item12);
                                        for (Player all : Server.getInstance().getOnlinePlayers().values()) {
                                            String name = all.getName();
                                            if (name.toLowerCase().equals(owner.toLowerCase())) {
                                                Server.getInstance().getPlayer(owner);

                                            }
                                        }

                                        timercfg.set("Timer." + hashcode, null);
                                        timercfg.save();

                                        pl.sendMessage(Auction.getAuctionConfig().prefix() + Auction.getAuctionConfig().buy());
                                        auccfg.set("Auction." + hashcode, null);
                                        auccfg.save();

                                    }

                                } else {
                                    pl.sendMessage(Auction.getAuctionConfig().prefix() + Auction.getAuctionConfig().money());
                                }

                            }
                        }
                    }

                    if (act.getInventory().getName().equals(Auction.getAuctionConfig().Storage())) {

                        Config timercfg = new Config(new File(Auction.getAuction().getDataFolder(), "/timer.yml"), Config.YAML);
                        timercfg.reload();

                        Config playercfg = new Config(new File(Auction.getAuction().getDataFolder(), "/playercfg.yml"), Config.YAML);
                        playercfg.reload();

                        Config countcfg = new Config(new File(Auction.getAuction().getDataFolder(), "/count.yml"), Config.YAML);
                        countcfg.reload();

                        interact(action.getSourceItem(), pl);

                        String owner = pl.getName().toLowerCase();
                        for (Map.Entry<String, Object> get2 : playercfg.getSections("Inventory." + owner).getAll().entrySet()) {
                            String get1 = get2.getKey();
                            String hashcode = playercfg.getString("Inventory." + owner + "." + get1 + ".Hash");
                            if (action.getSourceItem().getCustomName().contains(hashcode)) {

                                int price = playercfg.getInt("Inventory." + owner + "." + get1 + ".Cost");
                                int id = playercfg.getInt("Inventory." + owner + "." + get1 + ".Id");
                                int count1 = playercfg.getInt("Inventory." + owner + "." + get1 + ".Count");
                                int damege = playercfg.getInt("Inventory." + owner + "." + get1 + ".Damage");

                                double maincount = Auction.getAuction().getMoney(pl.getName());

                                if (maincount >= price) {

                                    int count = countcfg.getInt(owner);
                                    int result = count - 1;
                                    countcfg.set(owner, result);
                                    countcfg.save();

                                    double resultprice = maincount - price;
                                    Auction.getAuction().setMoney(pl, resultprice);
                                    Item item12 = Item.get(id, damege, count1);
                                    item12.setDamage(damege);

                                    for (String enchId : playercfg.getSection("Inventory." + owner + "." + get1 + ".Enchants").getKeys(false)) {
                                        int id1 = Integer.parseInt(enchId);
                                        int lvl = playercfg.getInt("Inventory." + owner + "." + get1 + ".Enchants." + enchId);
                                        item12.addEnchantment(Enchantment.getEnchantment(id1).setLevel(lvl, false));
                                    }

                                    pl.getInventory().addItem(item12);

                                    timercfg.set("Timer." + hashcode, null);
                                    timercfg.save();

                                    pl.sendMessage(Auction.getAuctionConfig().prefix() + Auction.getAuctionConfig().get());
                                    playercfg.set("Inventory." + owner + "." + hashcode, null);
                                    playercfg.save();
                                    playercfg.remove("Inventory." + owner + "." + hashcode);
                                    playercfg.save();

                                } else {
                                    pl.sendMessage(Auction.getAuctionConfig().prefix() + Auction.getAuctionConfig().money());
                                }

                            }
                        }

                    }

                }
            }
        }
    }

    private boolean interact(Item item, Player player) {

        String storage = Auction.getAuctionConfig().Storage();

        String title = Auction.getAuctionConfig().title();

        String next = Auction.getAuctionConfig().Next();
        String back = Auction.getAuctionConfig().Back();

        if (item.getId() == 339){
            if (item.getName().equals(next + "1")){
                ChestFakeInventory ec2 = new DoubleChestFakeInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest1);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "2")){
                ChestFakeInventory ec2 = new DoubleChestFakeInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest2);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "3")){
                ChestFakeInventory ec2 = new DoubleChestFakeInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest3);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "4")){
                ChestFakeInventory ec2 = new DoubleChestFakeInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest4);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "5")){
                ChestFakeInventory ec2 = new DoubleChestFakeInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest5);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "6")){
                ChestFakeInventory ec2 = new DoubleChestFakeInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest6);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "7")){
                ChestFakeInventory ec2 = new DoubleChestFakeInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest7);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "8")){
                ChestFakeInventory ec2 = new DoubleChestFakeInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest8);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "9")){
                ChestFakeInventory ec2 = new DoubleChestFakeInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest9);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "10")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest10);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "11")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest11);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "12")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest12);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "13")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest13);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "14")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest14);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "15")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest15);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "16")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest16);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "17")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest17);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "18")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest18);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "19")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest19);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(next + "20")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest20);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

        }

        if (item.getId() == 395){
            if (item.getName().equals(back + "0")){
                ChestFakeInventory ec2 = new DoubleChestFakeInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "1")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest1);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "2")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest2);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "3")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest3);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "4")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest4);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "5")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest5);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "6")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest6);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }
            if (item.getName().equals(back + "7")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest7);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "8")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest8);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "9")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest9);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "10")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest10);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "11")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest11);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "12")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest12);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "13")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest13);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "14")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest14);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "15")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest15);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "16")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest16);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "17")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest17);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "18")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest18);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }

            if (item.getName().equals(back + "19")){
                ChestFakeInventory ec2 = new FakeInventories().createDoubleChestInventory();
                ec2.setName(title);
                ec2.setTitle(title);
                ec2.setContents(Auction.getAuction().chest19);
                player.addWindow(ec2);
                ec2.addListener(this::onSlotChange);
            }


        }

        if (item.getId() == 54){
            if (item.getName().equals(storage)){

                Config playercfg = new Config(new File(Auction.getAuction().getDataFolder(), "/playercfg.yml"), Config.YAML);
                playercfg.reload();

                Int2ObjectOpenHashMap<Item> playerinv = new Int2ObjectOpenHashMap(); //Adds the chest items to this array

                String owner = player.getName().toLowerCase();

                int i = -1;
                for(Map.Entry<String, Object> get2 : playercfg.getSections("Inventory." + owner).getAll().entrySet()) {
                    String get1 = get2.getKey();
                    int price = playercfg.getInt("Inventory." + owner + "." + get1 + ".Cost");
                    String item1 = playercfg.getString("Inventory." + owner + "." + get1 + ".Items");
                    String owner1 = playercfg.getString("Inventory." + owner + "." + get1 + ".Owner");
                    int id = playercfg.getInt("Inventory." + owner + "." + get1 + ".Id");
                    int count1 = playercfg.getInt("Inventory." + owner + "." + get1 + ".Count");
                    int damege = playercfg.getInt("Inventory." + owner + "." + get1 + ".Damage");
                    String hashcode = playercfg.getString("Inventory." + owner + "." + get1 + ".Hash");
                    String date = playercfg.getString("Inventory." + owner + "." + hashcode + ".Date");

                    String textprice = Auction.getAuctionConfig().textprice();
                    String value = Auction.getAuctionConfig().value();
                    String timevalue = Auction.getAuctionConfig().timevalue();
                    String textowner = Auction.getAuctionConfig().textowner();
                    String textuntil = Auction.getAuctionConfig().textuntil();
                    String textdate = Auction.getAuctionConfig().textdate();
                    String texthash = Auction.getAuctionConfig().texthash();

                    Item item2 = Item.get(id, damege, count1).setCustomName(item1 + "\n\n" + textprice + price + value + "\n" + textowner + owner + "\n" + textuntil + "0" + timevalue + "\n" + texthash + hashcode + "\n" + textdate + date + "\n");
                    item2.setDamage(damege);
                    for (String enchId : playercfg.getSection("Inventory." + owner + "." + get1 + ".Enchants").getKeys(false)) {
                        int id1 = Integer.parseInt(enchId);
                        int lvl = playercfg.getInt("Inventory." + owner + "." + get1 + ".Enchants." + enchId);
                        item2.addEnchantment(Enchantment.getEnchantment(id1).setLevel(lvl, false));
                    }

                    i++;

                    if (i < Auction.getAuctionConfig().max()){
                        Item back1 = Item.get(351).setCustomName(Auction.getAuctionConfig().backstorage());
                        playerinv.put(49, back1);
                        playerinv.put(i, item2);
                    } else {
                        player.sendMessage("NA");
                    }


                }

                Item back1 = Item.get(351).setCustomName(Auction.getAuctionConfig().backstorage());
                playerinv.put(49, back1);

                ChestFakeInventory ec = new DoubleChestFakeInventory();
                ec.setName(storage);
                ec.setTitle(storage);
                ec.setContents(playerinv);
                player.addWindow(ec);
                ec.addListener(this::onSlotChange);

            }
        }

        if (item.getId() == 388){
            if (item.getName().equals(Auction.getAuctionConfig().Info())){
                player.sendMessage(Auction.getAuctionConfig().textinfo());
            }

        }

        if (item.getId() == 351){
            if (item.getName().equals(Auction.getAuctionConfig().backstorage())){

                Server.getInstance().dispatchCommand(player, "ah");

            }

        }

        return false;
    }

    public void onSlotChange(FakeSlotChangeEvent event) {

        String title = Auction.getAuctionConfig().title();
        String storage = Auction.getAuctionConfig().Storage();

        if (event.getInventory() instanceof DoubleChestFakeInventory) {
            if (event.getInventory().getName().equals(title)) {
                event.setCancelled(true);
            }
            if (event.getInventory().getName().equals(storage)) {
                event.setCancelled(true);
            }
        }

    }
}
