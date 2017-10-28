from django.shortcuts import render,render_to_response

from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse

from image_visualization.box_perspective import get_dimensions
from django.views.decorators.csrf import csrf_exempt

@csrf_exempt
def compute_image(request):
    import base64
    import json
    if request.method == "POST":
        image=  request.body.decode('utf-8')
        payload=json.loads(image)
        fh = open("D:/Users/s992177/Downloads/image_to_analyse.jpg", "wb")
        fh.write(base64.b64decode(payload.get("image")))
        fh.close()
        width,height,image=get_dimensions("D:/Users/s992177/Downloads/image_to_analyse.jpg")
        return JsonResponse({'width':width,"height":height,"image":"%s"%(image)})



