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
        // wTODO: Add lots of icons
        icons = Collections.unmodifiableList(Arrays.asList(
            new Icon(0, R.drawable.ic_face_black_24dp),
            new Icon(1, R.drawable.ic_favorite_black_24dp),
            new Icon(2, R.drawable.ic_home_black_24dp),
            new Icon(3, R.drawable.ic_lightbulb_outline_black_24dp)
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
