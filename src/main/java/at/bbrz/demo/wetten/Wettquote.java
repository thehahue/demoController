package at.bbrz.demo.wetten;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class Wettquote {

    @Column(name = "quote_gewinn")
    private BigDecimal gewinn;

    @Column(name = "quote_verlust")
    private BigDecimal verlust;

    @Column(name = "quote_unentschieden")
    private BigDecimal unentschieden;

    public Wettquote() {
    }

    public Wettquote(BigDecimal gewinn, BigDecimal verlust, BigDecimal unentschieden) {
        this.gewinn = gewinn;
        this.verlust = verlust;
        this.unentschieden = unentschieden;
    }

    public BigDecimal getGewinn() {
        return gewinn;
    }

    public void setGewinn(BigDecimal gewinn) {
        this.gewinn = gewinn;
    }

    public BigDecimal getVerlust() {
        return verlust;
    }

    public void setVerlust(BigDecimal verlust) {
        this.verlust = verlust;
    }

    public BigDecimal getUnentschieden() {
        return unentschieden;
    }

    public void setUnentschieden(BigDecimal unentschieden) {
        this.unentschieden = unentschieden;
    }
}
