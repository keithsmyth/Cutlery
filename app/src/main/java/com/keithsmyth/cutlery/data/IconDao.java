package com.keithsmyth.cutlery.data;

import com.keithsmyth.cutlery.R;
import com.keithsmyth.cutlery.model.Icon;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconDao {

    private static final Icon NO_ICON = new Icon(-1, R.drawable.ic_add_circle_outline_black_24dp);

    private final List<Icon> icons;
    private final Map<Integer, Icon> map;

    public IconDao() {
        icons = Collections.unmodifiableList(Arrays.asList(
            new Icon(0, R.drawable.ic_face_black_24dp),
            new Icon(1, R.drawable.ic_favorite_black_24dp),
            new Icon(2, R.drawable.ic_home_black_24dp),
            new Icon(3, R.drawable.ic_lightbulb_outline_black_24dp),
            new Icon(4, R.drawable.ic_accessibility_black_24dp),
            new Icon(5, R.drawable.ic_account_balance_black_24dp),
            new Icon(6, R.drawable.ic_assignment_turned_in_black_24dp),
            new Icon(7, R.drawable.ic_build_black_24dp),
            new Icon(8, R.drawable.ic_card_giftcard_black_24dp),
            new Icon(9, R.drawable.ic_card_travel_black_24dp),
            new Icon(10, R.drawable.ic_extension_black_24dp),
            new Icon(11, R.drawable.ic_flight_takeoff_black_24dp),
            new Icon(12, R.drawable.ic_pets_black_24dp),
            new Icon(13, R.drawable.ic_rowing_black_24dp),
            new Icon(14, R.drawable.ic_shopping_cart_black_24dp),
            new Icon(15, R.drawable.ic_watch_later_black_24dp),
            new Icon(16, R.drawable.ic_work_black_24dp),
            new Icon(17, R.drawable.ic_games_black_24dp),
            new Icon(18, R.drawable.ic_business_black_24dp),
            new Icon(19, R.drawable.ic_call_black_24dp),
            new Icon(20, R.drawable.ic_import_contacts_black_24dp),
            new Icon(21, R.drawable.ic_mail_outline_black_24dp),
            new Icon(22, R.drawable.ic_create_black_24dp),
            new Icon(23, R.drawable.ic_flag_black_24dp),
            new Icon(24, R.drawable.ic_weekend_black_24dp),
            new Icon(25, R.drawable.ic_airplanemode_active_black_24dp),
            new Icon(26, R.drawable.ic_attach_file_black_24dp),
            new Icon(27, R.drawable.ic_attach_money_black_24dp),
            new Icon(28, R.drawable.ic_insert_emoticon_black_24dp),
            new Icon(29, R.drawable.ic_cloud_queue_black_24dp),
            new Icon(30, R.drawable.ic_headset_black_24dp),
            new Icon(31, R.drawable.ic_speaker_black_24dp),
            new Icon(32, R.drawable.ic_brightness_5_black_24dp),
            new Icon(33, R.drawable.ic_brush_black_24dp),
            new Icon(34, R.drawable.ic_healing_black_24dp),
            new Icon(35, R.drawable.ic_directions_car_black_24dp),
            new Icon(36, R.drawable.ic_hotel_black_24dp),
            new Icon(37, R.drawable.ic_local_shipping_black_24dp),
            new Icon(38, R.drawable.ic_local_cafe_black_24dp),
            new Icon(39, R.drawable.ic_local_florist_black_24dp),
            new Icon(40, R.drawable.ic_traffic_black_24dp),
            new Icon(41, R.drawable.ic_wc_black_24dp),
            new Icon(42, R.drawable.ic_school_black_24dp),
            new Icon(43, R.drawable.ic_public_black_24dp),
            new Icon(44, R.drawable.ic_whatshot_black_24dp)
        ));
        map = new HashMap<>(icons.size());
        for (Icon icon : icons) {
            map.put(icon.id, icon);
        }
    }

    public List<Icon> list() {
        return icons;
    }

    public Icon get(int id) {
        return map.containsKey(id) ? map.get(id) : NO_ICON;
    }
}
