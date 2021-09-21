package com.epam.esm.data;

import com.epam.esm.util.SortMode;
import com.epam.esm.util.SortParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CertificateSelectionData {
    private String search;
    private int page;
    private int amount;
    private List<String> tags;
    private Map<SortParameter, SortMode> sorting;

    public CertificateSelectionData() {
    }

    public CertificateSelectionData(String search, int page, int amount, Map<String, String> sorting, List<String> tags) {
        this.search = search;
        this.page = page;
        this.amount = amount;
        this.tags = tags;
        setStringSorting(sorting);
    }

    public List<String> getTags() {
        if (tags == null){
            tags = new ArrayList<>();
        }
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getSearch() {
        if (search == null){
            search="";
        }
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Map<String, String> getStringSorting(){
        Map<String, String> stringSorting = new HashMap<>();
        for (Map.Entry<SortParameter, SortMode> sorts : sorting.entrySet()) {
            stringSorting.put(sorts.getKey().toString(), sorts.getValue().toString());
        }
        return stringSorting;
    }

    public Map<SortParameter, SortMode> getSorting() {
        return sorting;
    }

    public void setStringSorting(Map<String, String> sorting){
        this.sorting = determineParameters(sorting);
    }

    public void setSorting(Map<SortParameter, SortMode> sorting) {
        this.sorting = sorting;
    }

    private Map<SortParameter, SortMode> determineParameters(Map<String, String> sortTypes) {
        Map<SortParameter, SortMode> map = new HashMap<>();
        if (!sortTypes.isEmpty()) {
            for (Map.Entry<String, String> entry : sortTypes.entrySet()) {
                String field = entry.getKey();
                String modeString = entry.getValue();
                if (SortParameter.contains(field) && SortMode.contains(modeString)){
                    SortParameter parameter = SortParameter.valueOf(field.toUpperCase());
                    SortMode mode = SortMode.valueOf(modeString.toUpperCase());
                    map.put(parameter, mode);
                }
            }
        }
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CertificateSelectionData that = (CertificateSelectionData) o;

        if (page != that.page) return false;
        if (amount != that.amount) return false;
        if (search != null ? !search.equals(that.search) : that.search != null) return false;
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
        return sorting != null ? sorting.equals(that.sorting) : that.sorting == null;
    }

    @Override
    public int hashCode() {
        int result = search != null ? search.hashCode() : 0;
        result = 31 * result + page;
        result = 31 * result + amount;
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (sorting != null ? sorting.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CertificateSelectionData{");
        sb.append("search='").append(search).append('\'');
        sb.append(", page=").append(page);
        sb.append(", amount=").append(amount);
        sb.append(", tags=").append(tags);
        sb.append(", sorting=").append(sorting);
        sb.append('}');
        return sb.toString();
    }
}
