package com.bcp.training.expense;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.List;

@Path("/expenses")
public class ExpensesResource {

    @Inject
    ExpensesRepository expenses;

    @GET
    @Path("/{name}")
    public Expense getByName(@PathParam("name") String name) {
        Log.debug("Getting expense " + name);
        try {
            return expenses.getByName(name);
        } catch (ExpenseNotFoundException e) {
            var message = e.getMessage();
            Log.error(message);
            throw new NotFoundException(message);
        }
    }

    @GET
    public List<Expense> getAll() {
        return expenses.list();
    }

}
