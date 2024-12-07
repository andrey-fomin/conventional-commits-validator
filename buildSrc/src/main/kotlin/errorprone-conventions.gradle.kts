import net.ltgt.gradle.errorprone.errorprone
import org.gradle.kotlin.dsl.base

plugins {
    java
    id("net.ltgt.errorprone")
}

dependencies {
    annotationProcessor("com.uber.nullaway:nullaway:+")
    testAnnotationProcessor("com.uber.nullaway:nullaway:+")
    errorprone("com.google.errorprone:error_prone_core:+")
    implementation("com.google.errorprone:error_prone_annotations:+")
    implementation("com.github.spotbugs:spotbugs-annotations:+")
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone.disableWarningsInGeneratedCode.set(true)
    options.errorprone.error(
        "AnnotationPosition",
        "ArrayRecordComponent",
        "BooleanParameter",
        "CheckedExceptionNotThrown",
        "ConstantField",
        "ConstantPatternCompile",
        "FieldCanBeFinal",
        "FieldCanBeLocal",
        "FieldCanBeStatic",
        "FieldMissingNullable",
        "FloggerLogWithCause",
        "ForEachIterable",
        "GetClassOnEnum",
        "JavaTimeDefaultTimeZone",
        "LambdaFunctionalInterface",
        "MethodCanBeStatic",
        "MissingBraces",
        "MissingDefault",
        "NonApiType",
        "NullAway",
        "PackageLocation",
        "ParameterMissingNullable",
        "PrivateConstructorForUtilityClass",
        "RedundantThrows",
        "RemoveUnusedImports",
        "ReturnMissingNullable",
        "StatementSwitchToExpressionSwitch",
        "SuppressWarningsWithoutExplanation",
        "SwitchDefault",
        "SystemOut",
        "ThrowsUncheckedException",
        "TryWithResourcesVariable",
        "TypeParameterNaming",
        "UngroupedOverloads",
        "UnnecessaryAnonymousClass",
        "UnnecessaryBoxedAssignment",
        "UnnecessaryBoxedVariable",
        "UnnecessaryDefaultInEnumSwitch",
        "UnnecessaryFinal",
        "UnnecessaryLambda",
        "UnnecessaryParentheses",
        "UnnecessaryStaticImport",
        "UnusedException",
        "UnusedMethod",
        "UnusedVariable",
        "UseEnumSwitch",
        "Var",
        "VoidUsed",
        "WildcardImport",
    )
    options.errorprone.option("NullAway:AnnotatedPackages", "io.github.fomin.conventionalcommits")
}
