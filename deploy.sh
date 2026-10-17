#!/bin/bash
# ==========================================================
# SATARK - Quick Compile & Tomcat Deployment Script
# ==========================================================

TOMCAT_DIR="/Users/harishanandh/Downloads/apache-tomcat-9.0.93"

echo "--> [1/3] Compiling SATARK Servlets & Hibernate Entities..."
javac -cp "$TOMCAT_DIR/lib/servlet-api.jar:lib/*" \
      -d webapp/WEB-INF/classes \
      src/com/satark/model/*.java \
      src/com/satark/util/*.java \
      src/com/satark/dao/*.java \
      src/com/satark/servlet/*.java \
      src/com/disaster/*.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "--> [2/3] Syncing Hibernate configuration to classes..."
cp src/hibernate.cfg.xml webapp/WEB-INF/classes/

echo "--> [3/3] Deploying to Tomcat webapps..."
mkdir -p "$TOMCAT_DIR/webapps/satark"
cp -r webapp/* "$TOMCAT_DIR/webapps/satark/"

echo ""
echo "✅ SATARK is successfully compiled and deployed!"
echo "🌐 Open in browser: http://localhost:8080/satark/"
