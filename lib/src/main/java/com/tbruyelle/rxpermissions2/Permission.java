package com.tbruyelle.rxpermissions2;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * 权限
 *
 * @author maple
 * @time 2018/12/26
 */
public class Permission {
    public final String name;// 权限名
    public final boolean granted;// 是否同意
    public final boolean neverAskAgain;// 是否永不再问（被拒绝过一次，是否勾选“下次不在询问”）

    public Permission(String name, boolean granted) {
        this(name, granted, false);
    }

    public Permission(String name, boolean granted, boolean neverAskAgain) {
        this.name = name;
        this.granted = granted;
        this.neverAskAgain = neverAskAgain;
    }

    public Permission(List<Permission> permissions) {
        name = combineName(permissions);
        granted = combineGranted(permissions);
        neverAskAgain = combineNeverAskAgain(permissions);
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Permission that = (Permission) o;

        if (granted != that.granted) return false;
        if (neverAskAgain != that.neverAskAgain)
            return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (granted ? 1 : 0);
        result = 31 * result + (neverAskAgain ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", granted=" + granted +
                ", neverAskAgain=" + neverAskAgain +
                '}';
    }

    // 结合 name
    private String combineName(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .map(new Function<Permission, String>() {
                    @Override
                    public String apply(Permission permission) {
                        return permission.name;
                    }
                }).collectInto(new StringBuilder(), new BiConsumer<StringBuilder, String>() {
                    @Override
                    public void accept(StringBuilder s, String s2) {
                        if (s.length() == 0) {
                            s.append(s2);
                        } else {
                            s.append(", ").append(s2);
                        }
                    }
                }).blockingGet().toString();
    }

    private Boolean combineGranted(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .all(new Predicate<Permission>() {
                    @Override
                    public boolean test(Permission permission) {
                        return permission.granted;
                    }
                }).blockingGet();
    }

    private Boolean combineNeverAskAgain(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .any(new Predicate<Permission>() {
                    @Override
                    public boolean test(Permission permission) {
                        return permission.neverAskAgain;
                    }
                }).blockingGet();
    }
}
