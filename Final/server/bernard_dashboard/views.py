from django.shortcuts import render,render_to_response

from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse

from image_visualization.box_perspective import get_dimensions
from django.views.decorators.csrf import csrf_exempt

@csrf_exempt
def compute_image(request):
    import base64
    import json
    import os

    dir_path = os.path.dirname(os.path.realpath(__file__))
    if request.method == "POST":
        image=  request.body.decode('latin1')[2:]
        print (image)
        payload=json.loads(image)
        fh = open("%s/%s"%(dir_path,"image_to_analyse.jpg"), "wb")
        fh.write(base64.b64decode(payload.get("image")))
        fh.close()
        width,height,image=get_dimensions("%s/%s"%(dir_path,"image_to_analyse.jpg"))
        return JsonResponse({'width':width,"height":height,"image":"%s"%(image.decode())})



