package com.keithsmyth.cutlery.data;

import com.keithsmyth.cutlery.model.Icon;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class IconDaoTest {

    @Test
    public void test_no_ids_repeated() {
        // arrange
        final IconDao iconDao = new IconDao();
        // act
        final Set<Integer> idSet = new HashSet<>();
        for (Icon icon : iconDao.list()) {
            // assert
            if (idSet.contains(icon.id)) {
                fail("ID " + icon.id + " has been repeated");
            }
            idSet.add(icon.id);
        }
    }
}