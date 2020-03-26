#!/bin/bash

echo ""
echo "Applying migration Confirmation"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /:mrn/confirmation                       controllers.ConfirmationController.onPageLoad(mrn: MovementReferenceNumber)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "confirmation.title = confirmation" >> ../conf/messages.en
echo "confirmation.heading = confirmation" >> ../conf/messages.en

echo "Migration Confirmation completed"
