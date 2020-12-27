package ru.bio4j.spring.commons.utils;

import org.junit.Assert;
import org.junit.Test;

public class ListsTest {

    @Test
    public void anyRoleInList1() {
        Assert.assertTrue(Lists.anyInList("3,4", "2,3,6,8,12"));
        Assert.assertFalse(Lists.anyInList("1,9", "2,3,6,8,12"));
    }
    @Test
    public void anyRoleInList2() {
        Assert.assertTrue(Lists.anyInList(new int[]{3,4}, "2,3,6,8,12"));
        Assert.assertFalse(Lists.anyInList(new int[]{1,9}, "2,3,6,8,12"));
    }
    @Test
    public void anyRoleInList3() {
        Assert.assertTrue(Lists.anyInList(new int[]{3,4}, new int[]{2,3,6,8,12}));
        Assert.assertFalse(Lists.anyInList(new int[]{1,9}, new int[]{2,3,6,8,12}));
    }
    @Test
    public void anyRoleInList4() {
        Assert.assertTrue(Lists.itemInList(3L, new long[]{2,3,6,8,12}));
        Assert.assertFalse(Lists.itemInList(1L, new long[]{2,3,6,8,12}));
    }
    @Test
    public void anyRoleInList5() {
        Assert.assertTrue(Lists.itemInList(3, "2,3,6,8,12"));
        Assert.assertFalse(Lists.itemInList(1, "2,3,6,8,12"));
    }
    @Test
    public void anyRoleInList6() {
        Assert.assertTrue(Lists.itemInList("3", new int[]{2,3,6,8,12}));
        Assert.assertFalse(Lists.itemInList("1", new int[]{2,3,6,8,12}));
    }
    @Test
    public void anyRoleInList7() {
        Assert.assertTrue(Lists.itemInList("3", "2,3,6,8,12"));
        Assert.assertFalse(Lists.itemInList("1", "2,3,6,8,12"));
    }
}