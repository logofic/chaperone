(ns ^{:doc "Core macros for angular usage and the front end of the site"}
    chaperone.ng.core)

(defmacro ng-apply
    "Easy way to do a Angular $scope.apply "
    [$scope & body]
    `(.$apply ~$scope (fn [] ~@body)))
