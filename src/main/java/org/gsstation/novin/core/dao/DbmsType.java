package org.gsstation.novin.core.dao;

/**
 * Created by A_Tofigh at 07/19/2024
 */
public enum DbmsType {
    ORACLE("oracle"),
    SQLSERVER("sqlserver", new String[]{"sql-server", "mssql-server"}),
    MYSQL("mysql", new String[]{"my-sql"});

    private String name;
    private String[] aliases;

    DbmsType(String name, String[] aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    DbmsType(String name) {
        this(name, new String[0]);
    }

    public static DbmsType fromName(String name) {
        for (DbmsType dbmsType : DbmsType.values()) {
            if (dbmsType.name.equalsIgnoreCase(name))
                return dbmsType;
            for (String alias : dbmsType.aliases) {
                if (dbmsType.name.equalsIgnoreCase(alias))
                    return dbmsType;
            }
        }
        // default DBMS is considered Oracle
        return ORACLE;
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }
}
