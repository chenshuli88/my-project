package base.excel;


public class ExcelErrorVo {
    private int rowX;
    private String cellY;
    private String content;

    public ExcelErrorVo() {
    }

    public ExcelErrorVo(int rowX, String cellY, String content) {
        this.rowX = rowX;
        this.cellY = cellY;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ExcelErrorVo that = (ExcelErrorVo)o;
            if (this.rowX != that.rowX) {
                return false;
            } else {
                label38: {
                    if (this.cellY != null) {
                        if (this.cellY.equals(that.cellY)) {
                            break label38;
                        }
                    } else if (that.cellY == null) {
                        break label38;
                    }

                    return false;
                }

                boolean var10000;
                label53: {
                    if (this.content != null) {
                        if (this.content.equals(that.content)) {
                            break label53;
                        }
                    } else if (that.content == null) {
                        break label53;
                    }

                    var10000 = false;
                    return var10000;
                }

                var10000 = true;
                return var10000;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = this.rowX;
        result = 31 * result + (this.cellY != null ? this.cellY.hashCode() : 0);
        result = 31 * result + (this.content != null ? this.content.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExcelErrorVo{rowX=" + this.rowX + ", cellY='" + this.cellY + '\'' + ", content='" + this.content + '\'' + '}';
    }

    public int getRowX() {
        return this.rowX;
    }

    public void setRowX(int rowX) {
        this.rowX = rowX;
    }

    public String getCellY() {
        return this.cellY;
    }

    public void setCellY(String cellY) {
        this.cellY = cellY;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
