'use strict';

quizApp.config(["$routeProvider",function($routeProvider){
    $routeProvider.
            when("/",{
                controller:"homeCtrl",
                templateUrl:"app/views/Home.html"
            })
            .when("/CoursUI",{
                controller:"CoursController",
                templateUrl:"app/views/Cours.html"
            }) 
             .when("/SubscribersUI",{
                controller:"SubscribersController",
                templateUrl:"app/views/Subscribers.html"
            })
            .when("/UsersUI",{
                controller:"homeCtrl",
                templateUrl:"app/views/Home.html"
  
            })
            .when("/QuestionsUI",{
                controller:"questionCtrl",
                templateUrl:"app/views/QuestionUI.html"
            })
            .when("/SessionUI",{
                templateUrl:"app/views/Home.html"
            })
            .when("/ProfilUI",{
                controller:"profilCtrl",
                templateUrl:"app/views/ProfilUI.html"
            })
        
    ;
            
   
    
}]);

