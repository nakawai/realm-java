package com.tightdb.example;

import java.util.*;

import com.tightdb.*;
import com.tightdb.lib.Table;
import com.tightdb.example.generated.EmployeeTable;

public class ReplicationExample {

    @Table
    class employee {
        String firstName;
        String lastName;
        int salary;
        boolean driver;
        byte[] photo;
        Date birthdate;
        Object extra;
        phone phones;
    }

    @Table
    class phone {
        String type;
        String number;
    }

    public static void main(String[] args)
    {
        String databaseFile = SharedGroupWithReplication.getDefaultDatabaseFileName();

        ArrayDeque<String> positionalArgs = new ArrayDeque<String>();
        boolean error = false;
        for (int i=0; i<args.length; ++i) {
            String arg = args[i];
            if (arg.length() < 2 || !arg.substring(0,2).equals("--")) {
                positionalArgs.addLast(arg);
                continue;
            }

            if (arg.equals("--database-file")) {
                if (i+1 < args.length) {
                    databaseFile = args[++i];
                    continue;
                }
            }
            error = true;
            break;
        }
        if (error || positionalArgs.size() != 0) {
            System.err.println(
                "ERROR: Bad command line.\n\n" +
                "Synopsis: java com.tightdb.example.ReplicationExample\n\n" +
                "Options:\n" +
                "  --database-file STRING   (default: \""+databaseFile+"\")");
            System.exit(1);
        }

        SharedGroup db = new SharedGroupWithReplication(databaseFile);
        try {
            WriteTransaction transact = db.beginWrite();
            try {
                EmployeeTable employees = new EmployeeTable(transact);
                employees.add("John", "Doe", 10000, true,
                              new byte[] { 1, 2, 3 }, new Date(), "extra");
                System.out.println(employees.size());
                transact.commit();
                System.err.println("CLICK-1");
            }
            catch (Throwable e) {
                System.err.println("CLICK-2");
                transact.rollback();
                System.err.println("CLICK-3");
                throw e;
            }
        }
        finally {
            System.err.println("CLICK-4");
            db.close();
            System.err.println("CLICK-5");
        }
    }
}
