#!/bin/bash

# Default to development if no environment specified
ENVIRONMENT=${ENVIRONMENT:-development}

# Define target directories
SAMPLEJAVA_DIR="samplejava"
SAMPLEKOTLIN_DIR="samplekotlin"
TEMPLATE_FILE="google-services.json.template"

# Check for required environment variables
if [ -z "$GOOGLE_API_KEY" ]; then
    echo "Error: GOOGLE_API_KEY environment variable is not set"
    exit 1
fi

if [ -z "$GOOGLE_PROJECT" ]; then
    echo "Error: GOOGLE_PROJECT environment variable is not set"
    exit 1
fi

# Function to generate config file
generate_config() {
    local target_dir=$1
    local target_file="$target_dir/google-services.json"
    
    # Check if target directory exists
    if [ ! -d "$target_dir" ]; then
        echo "Warning: Directory $target_dir does not exist, skipping..."
        return
    fi
    
    # Create directory if it doesn't exist
    mkdir -p "$target_dir"
    
    # Replace placeholders with actual values
    sed -e "s/\${GOOGLE_API_KEY}/$GOOGLE_API_KEY/g" \
        -e "s/\${GOOGLE_PROJECT}/$GOOGLE_PROJECT/g" \
        "$target_dir/$TEMPLATE_FILE" > "$target_file"
    
    if [ $? -eq 0 ]; then
        echo "✓ Generated: $target_file"
    else
        echo "✗ Failed to generate: $target_file"
        exit 1
    fi
}

echo "Generating google-services.json files..."
echo "Environment: $ENVIRONMENT"
echo "Template: $TEMPLATE_FILE"
echo "Project: $GOOGLE_PROJECT"
echo "API Key: ${GOOGLE_API_KEY:0:10}..."
echo ""

# Generate config for both directories
generate_config "$SAMPLEJAVA_DIR"
generate_config "$SAMPLEKOTLIN_DIR"

echo ""
echo "All google-services.json files generated successfully!"

# Optional: Verify the generated files
echo ""
echo "Verification:"
for dir in "$SAMPLEJAVA_DIR" "$SAMPLEKOTLIN_DIR"; do
    if [ -f "$dir/google-services.json" ]; then
        echo "✓ $dir/google-services.json exists"
        # Show project_id from generated file
        project_id=$(grep -o '"project_id": "[^"]*"' "$dir/google-services.json" | cut -d'"' -f4)
        echo "  Project ID: $project_id"
    else
        echo "✗ $dir/google-services.json missing"
    fi
done