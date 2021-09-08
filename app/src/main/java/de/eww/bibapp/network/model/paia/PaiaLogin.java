package de.eww.bibapp.network.model.paia;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.PaiaHelper;

public class PaiaLogin {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private int expiresIn;

    @SerializedName("scope")
    private String scope;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("patron")
    private String patron;

    private List<SCOPES> scopes;

    public enum SCOPES {
        READ_PATRON,
        READ_FEES,
        READ_ITEMS,
        WRITE_ITEMS
    }

    public String getAccessToken() {
        return accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public List<SCOPES> getScopes() {
        if (this.scopes == null) {
            this.scopes = new ArrayList<>();

            String[] scopes = scope.split(" ");

            if (scopes.length > 0) {
                for (String scope: scopes) {
                    if (scope.equals("read_patron")) {
                        this.scopes.add(SCOPES.READ_PATRON);
                    } else if (scope.equals("read_fees")) {
                        this.scopes.add(SCOPES.READ_FEES);
                    } else if (scope.equals("read_items")) {
                        this.scopes.add(SCOPES.READ_ITEMS);
                    } else if (scope.equals("write_items")) {
                        this.scopes.add(SCOPES.WRITE_ITEMS);
                    }
                }
            }
        }

        return this.scopes;
    }

    public boolean hasScope(SCOPES scope) {
        return getScopes().contains(scope);
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getPatron() {
        return patron;
    }
}
