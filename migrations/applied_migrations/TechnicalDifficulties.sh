#!/bin/bash

echo ""
echo "Applying migration TechicalDifficulties"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /techicalDifficulties                       controllers.TechicalDifficultiesController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "techicalDifficulties.title = techicalDifficulties" >> ../conf/messages.en
echo "techicalDifficulties.heading = techicalDifficulties" >> ../conf/messages.en

echo "Migration TechicalDifficulties completed"
