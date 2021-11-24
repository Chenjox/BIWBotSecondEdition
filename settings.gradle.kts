rootProject.name = "BIWBotSecondEdition"
include(":extensions:date",":extensions:cloud")
include(
    ":extensions:activity:database", // backend
    ":extensions:activity:collector", // frontend
    ":extensions:activity:evaluator" // statistics
    )
include(":discord:bot")