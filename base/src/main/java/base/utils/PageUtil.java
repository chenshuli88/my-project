package base.utils;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/3 16:52
 * @description
 * @since 2.8.1
 */
public class PageUtil {
    private int page = 1;
    private int rows = 20;
    private int totalRows;
    private String orderBy;
    private Order order;
    private String sort;

    public PageUtil() {
    }

    public int getStart() {
        return (this.page - 1) * this.rows;
    }

    public int getEnd() {
        return this.page * this.rows;
    }

    public int getPageTotal() {
        return this.totalRows == 0 ? 0 : this.totalRows / this.rows + (this.totalRows % this.rows == 0 ? 0 : 1);
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        if (page >= 1) {
            this.page = page;
        }

    }

    public int getRows() {
        return this.rows;
    }

    public void setRows(int rows) {
        if (rows >= 1) {
            this.rows = rows;
        }

    }

    public int getTotalRows() {
        return this.totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public String getOrderBy() {
        return this.orderBy == null && this.sort != null ? StringUtils.camel2UnderScore(this.sort) : this.orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = StringUtils.unhtml(orderBy);
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getSort() {
        return this.sort;
    }

    public void setSort(String sort) {
        this.sort = StringUtils.unhtml(sort);
    }

    public static enum Order {
        asc("asc"),
        desc("desc"),
        ASC("ASC"),
        DESC("DESC");

        private String order;

        private Order(String order) {
            this.order = order;
        }

        @Override
        public String toString() {
            return this.order.toLowerCase();
        }
    }
}
