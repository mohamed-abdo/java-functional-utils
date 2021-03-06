package softideas.funcs;
class utils{
public final Stream<?> getResultsInFuture(Supplier<?>... functors) {
        CompletableFuture[] promises = Stream.of(functors)
                .map(CompletableFuture::supplyAsync)
                .collect(Collectors.toList())//serialize | projection operation
                .toArray(new CompletableFuture[functors.length]);
        try {
            return CompletableFuture.allOf(promises)
                    .thenApply(p -> stream(promises)
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()))
                    .handle((results, ex) -> {
                        if (ex != null) {
                            logger.error("an error occurred during getting a future results", ex);
                            return new ArrayList<>();
                        }
                        return results;
                    })
                    .get()
                    .stream();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("an error occurred during executing a function", e);
            return Stream.of(null);
        }
    }
}
