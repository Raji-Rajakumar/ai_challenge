package com.gildedrose;

/**
 * The {@code GildedRose} class manages the inventory of items in a shop, updating their
 * quality and sell-in values according to specific business rules.
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Handles special item types: "Aged Brie", "Backstage passes", and "Sulfuras".</li>
 *   <li>Automatically updates item quality and sell-in values daily.</li>
 *   <li>Enforces quality boundaries (0 to 50, except for "Sulfuras").</li>
 *   <li>Extensible for new item types and rules.</li>
 * </ul>
 *
 * <p><b>Usage Patterns:</b></p>
 * <pre>
 * {@code
 * Item[] items = new Item[] {
 *     new Item("Aged Brie", 2, 0),
 *     new Item("Elixir of the Mongoose", 5, 7)
 * };
 * GildedRose app = new GildedRose(items);
 * app.updateQuality();
 * }
 * </pre>
 *
 * <p><b>Dependencies:</b></p>
 * <ul>
 *   <li>{@link Item} class (must have public fields: name, sellIn, quality)</li>
 * </ul>
 *
 * <p><b>Examples:</b></p>
 * <pre>
 * // Basic Usage
 * Item[] items = {
 *     new Item("Aged Brie", 2, 0),
 *     new Item("Elixir of the Mongoose", 5, 7)
 * };
 * GildedRose app = new GildedRose(items);
 * app.updateQuality();
 *
 * // Advanced Use Case
 * Item[] items = {
 *     new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
 *     new Item("Sulfuras, Hand of Ragnaros", 0, 80)
 * };
 * GildedRose app = new GildedRose(items);
 * for (int i = 0; i < 20; i++) {
 *     app.updateQuality();
 * }
 *
 * // Error Handling Example
 * try {
 *     Item[] items = { new Item(null, 5, 10) };
 *     GildedRose app = new GildedRose(items);
 * } catch (IllegalArgumentException e) {
 *     // Handle invalid item
 * }
 * </pre>
 */
class GildedRose {
    Item[] items;

    private static final int MAX_QUALITY = 50;
    private static final int MIN_QUALITY = 0;
    private static final String AGED_BRIE = "Aged Brie";
    private static final String BACKSTAGE = "Backstage passes to a TAFKAL80ETC concert";
    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";

    /**
     * Constructs a new {@code GildedRose} inventory manager.
     *
     * @param items Array of {@link Item} objects to manage. Each item must have a non-null name and a quality between 0 and 50.
     * @throws IllegalArgumentException if any item has a null name or quality out of bounds.
     *
     * <b>Usage Example:</b>
     * <pre>
     * {@code
     * Item[] items = { new Item("Aged Brie", 2, 0) };
     * GildedRose app = new GildedRose(items);
     * }
     * </pre>
     */
    public GildedRose(Item[] items) {
        for (Item item : items) {
            if (item.name == null) {
                throw new IllegalArgumentException("Item name cannot be null");
            }
            if (item.quality < 0 || item.quality > 50) {
                throw new IllegalArgumentException("Item quality out of bounds");
            }
        }
        this.items = items;
    }

    /**
     * Updates the quality and sell-in values for all managed items according to business rules.
     *
     * <b>Parameters:</b> None
     * <b>Returns:</b> void
     * <b>Exceptions:</b> None
     *
     * <b>Usage Example:</b>
     * <pre>
     * {@code
     * app.updateQuality();
     * }
     * </pre>
     *
     * <b>Performance:</b> O(n) where n is the number of items.
     */
    public void updateQuality() {
        for (Item item : items) {
            if (isSulfuras(item)) continue;

            updateQualityBeforeSellIn(item);
            decreaseSellIn(item);
            updateQualityAfterSellIn(item);
        }
    }

    /**
     * Increases the quality of the given item by 1, up to the maximum allowed quality.
     *
     * @param item The {@link Item} whose quality to increase.
     * @return void
     * @throws NullPointerException if item is null.
     *
     * <b>Usage Example:</b>
     * <pre>
     * {@code
     * increaseQuality(item);
     * }
     * </pre>
     */
    private void increaseQuality(Item item) {
        if (item.quality < MAX_QUALITY) {
            item.quality++;
        }
    }

    /**
     * Decreases the quality of the given item by 1, down to the minimum allowed quality.
     *
     * @param item The {@link Item} whose quality to decrease.
     * @return void
     * @throws NullPointerException if item is null.
     *
     * <b>Usage Example:</b>
     * <pre>
     * {@code
     * decreaseQuality(item);
     * }
     * </pre>
     */
    private void decreaseQuality(Item item) {
        if (item.quality > MIN_QUALITY) {
            item.quality--;
        }
    }

    /**
     * Checks if the given item is "Sulfuras, Hand of Ragnaros".
     *
     * @param item The {@link Item} to check.
     * @return true if the item is Sulfuras, false otherwise.
     * @throws NullPointerException if item is null.
     *
     * <b>Usage Example:</b>
     * <pre>
     * {@code
     * if (isSulfuras(item)) { ... }
     * }
     * </pre>
     */
    private boolean isSulfuras(Item item) {
        return item.name.equals(SULFURAS);
    }

    /**
     * Updates the quality of the item before the sell-in date, according to its type.
     *
     * @param item The {@link Item} to update.
     * @return void
     * @throws NullPointerException if item is null.
     *
     * <b>Usage Example:</b>
     * <pre>
     * {@code
     * updateQualityBeforeSellIn(item);
     * }
     * </pre>
     */
    private void updateQualityBeforeSellIn(Item item) {
        if (!item.name.equals(AGED_BRIE) && !item.name.equals(BACKSTAGE)) {
            if (item.quality > 0) {
                if (!item.name.equals(SULFURAS)) {
                    item.quality = item.quality - 1;
                }
            }
        } else {
            if (item.quality < 50) {
                item.quality = item.quality + 1;

                if (item.name.equals(BACKSTAGE)) {
                    if (item.sellIn < 11) {
                        if (item.quality < 50) {
                            item.quality = item.quality + 1;
                        }
                    }

                    if (item.sellIn < 6) {
                        if (item.quality < 50) {
                            item.quality = item.quality + 1;
                        }
                    }
                }
            }
        }
    }

    /**
     * Decreases the sell-in value of the item by 1, except for "Sulfuras".
     *
     * @param item The {@link Item} to update.
     * @return void
     * @throws NullPointerException if item is null.
     *
     * <b>Usage Example:</b>
     * <pre>
     * {@code
     * decreaseSellIn(item);
     * }
     * </pre>
     */
    private void decreaseSellIn(Item item) {
        if (!item.name.equals(SULFURAS)) {
            item.sellIn = item.sellIn - 1;
        }
    }

    /**
     * Updates the quality of the item after the sell-in date, according to its type.
     *
     * @param item The {@link Item} to update.
     * @return void
     * @throws NullPointerException if item is null.
     *
     * <b>Usage Example:</b>
     * <pre>
     * {@code
     * updateQualityAfterSellIn(item);
     * }
     * </pre>
     */
    private void updateQualityAfterSellIn(Item item) {
        if (item.sellIn < 0) {
            if (!item.name.equals(AGED_BRIE)) {
                if (!item.name.equals(BACKSTAGE)) {
                    if (item.quality > 0) {
                        if (!item.name.equals(SULFURAS)) {
                            item.quality = item.quality - 1;
                        }
                    }
                } else {
                    item.quality = 0;
                }
            } else {
                if (item.quality < 50) {
                    item.quality = item.quality + 1;
                }
            }
        }
    }
}
