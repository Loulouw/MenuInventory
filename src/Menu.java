import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class Menu implements Listener {

    private static ArrayList<Player> listPlayer = new ArrayList<>();
    private Inventory inventory;
    private HashMap<Integer, onClick> contents = new HashMap<>();

    /******************************************
     * Constructor region
     */

    public Menu(int numberOfRows, String name, Player owner) {
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugins()[0]);
        inventory = Bukkit.createInventory(owner, checkNumberOfRows(numberOfRows) * 9, name);
    }

    public Menu(int numberOfRows, String name) {
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugins()[0]);
        inventory = Bukkit.createInventory(null, checkNumberOfRows(numberOfRows) * 9, name);
    }

    public Menu(int numberOfRows) {
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugins()[0]);
        inventory = Bukkit.createInventory(null, checkNumberOfRows(numberOfRows) * 9);
    }

    /******************************************
     * Public region
     */

    public ArrayList<Tuple<Integer, Integer>> getPositionsOfItemStack(ItemStack itemStack) {
        ArrayList<Tuple<Integer,Integer>> res = new ArrayList<>();
        for (int i = 0; i < inventory.getContents().length; i++) {
            if (itemStack.equals(inventory.getItem(i))) {
                res.add(getRowAndPos(i));
            }
        }
        return res;
    }

    public void addItem(ItemStack itemStack, int row, int position) {
        inventory.setItem(getSlot(row,position), itemStack);
    }

    public void addItemAction(ItemStack itemStack, int row, int position, onClick click){
        inventory.setItem(getSlot(row,position), itemStack);
        contents.put(position,click);
    }

    public void openInventory(Player p){
        listPlayer.add(p);
        p.openInventory(inventory);
    }

    public interface onClick {
        boolean click(ClickType clickType, Player player, Inventory inventory, int slot, ItemStack item);
    }

    /******************************************
     * Listener region
     */

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event){
        for(Player p : listPlayer) p.closeInventory();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        listPlayer.remove(event.getPlayer());
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getInventory() != null && event.getCurrentItem() != null){
            contents.get(event.getSlot()).click(event.getClick(),(Player)event.getWhoClicked(),inventory,event.getSlot(),event.getCurrentItem());
            event.setCancelled(true);
        }
    }

    /******************************************
     * Private region
     */

    private int checkNumberOfRows(int numberOfRows) {
        if (numberOfRows > 6) {
            numberOfRows = 6;
        } else if (numberOfRows <= 0) {
            numberOfRows = 1;
        }
        return numberOfRows;
    }

    private int getSlot(int row,int pos){
        if (row <= 0) {
            row = 1;
        } else if (row > (inventory.getSize() + 1) / 9) {
            row = (inventory.getSize() + 1) / 9;
        }

        if (pos <= 0) {
            pos = 1;
        } else if (pos > 9) {
            pos = 9;
        }
        return row * pos -1;
    }

    private Tuple<Integer,Integer> getRowAndPos(int slot){
        int row = slot / 9 + 1;
        int pos = slot % 9 + 1;
        return new Tuple<>(row,pos);
    }

}
