package classifier;

public class LexemProbabilities {

    private String lexem;
    private double auto;
    private double sportas;
    private double verslas;
    private double mokslas;

    public LexemProbabilities(String lexem) {
        this.lexem = lexem;
    }

    public LexemProbabilities() {
        this.auto = 0.4;
        this.sportas = 0.4;
        this.verslas = 0.4;
        this.mokslas = 0.4;
    }

    public String getLexem() {
        return lexem;
    }

    public void setLexem(String lexem) {
        this.lexem = lexem;
    }

    public double getAuto() {
        return auto;
    }

    public void setAuto(double auto) {
        this.auto = auto;
    }

    public double getSportas() {
        return sportas;
    }

    public void setSportas(double sportas) {
        this.sportas = sportas;
    }

    public double getVerslas() {
        return verslas;
    }

    public void setVerslas(double verslas) {
        this.verslas = verslas;
    }

    public double getMokslas() {
        return mokslas;
    }

    public void setMokslas(double mokslas) {
        this.mokslas = mokslas;
    }

    public boolean isAutoMax() {
        if (auto < sportas || auto < verslas || auto < mokslas) {
            return false;
        }
        return true;
    }

    public boolean isSportasMax() {
        if (sportas < auto || sportas < verslas || sportas < mokslas) {
            return false;
        }
        return true;
    }

    public boolean isVerslasMax() {
        if (verslas < auto || verslas < sportas || verslas < mokslas) {
            return false;
        }
        return true;
    }

    public boolean isMokslasMax() {
        if (mokslas < auto || mokslas < sportas || mokslas < verslas) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return lexem + ";" + auto + ";" + sportas + ";" + verslas + ";" + mokslas;
    }

    @Override
    public int hashCode() {
        return lexem.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        LexemProbabilities other = (LexemProbabilities) obj;
        return lexem.equals(obj);
    }
}
