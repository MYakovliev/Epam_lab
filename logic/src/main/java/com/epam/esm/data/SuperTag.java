package com.epam.esm.data;

import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;

public class SuperTag {
    private Tag superTag;
    private User superUser;

    public SuperTag(Tag superTag, User superUser) {
        this.superTag = superTag;
        this.superUser = superUser;
    }

    public Tag getSuperTag() {
        return superTag;
    }

    public void setSuperTag(Tag superTag) {
        this.superTag = superTag;
    }

    public User getSuperUser() {
        return superUser;
    }

    public void setSuperUser(User superUser) {
        this.superUser = superUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuperTag superTag1 = (SuperTag) o;

        if (superTag != null ? !superTag.equals(superTag1.superTag) : superTag1.superTag != null) return false;
        return superUser != null ? superUser.equals(superTag1.superUser) : superTag1.superUser == null;
    }

    @Override
    public int hashCode() {
        int result = superTag != null ? superTag.hashCode() : 0;
        result = 31 * result + (superUser != null ? superUser.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SuperTag{");
        sb.append("superTag=").append(superTag);
        sb.append(", superUser=").append(superUser);
        sb.append('}');
        return sb.toString();
    }
}
