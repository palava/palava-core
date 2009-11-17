/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.cosmocode.palava.components.captcha;

import java.awt.Color;
import java.awt.image.ImageFilter;

import org.jdom.Element;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.BaffleTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;

import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.Server;


public class PalavaGimpyEngine extends ListImageCaptchaEngine implements de.cosmocode.palava.Component {


	double amplitude = 3d;
	boolean antialias = true;
	double phase = 20d;
	double wavelength = 70d;
	Color textColor = Color.black; // TODO: configure color by name
	Color bgColor = Color.white; // TODO: configure color by name
	int width = 200;
	int height = 100;
	int fontMinHeight = 30;
	int fontMaxHeight = 35;
    int numberOfHoles = 1;
	
    protected void buildInitialFactories() {
        this.addFactory( createFactory() );
    }
    
    protected GimpyFactory createFactory () {
        //build filters
        //

        com.jhlabs.image.WaterFilter water = new com.jhlabs.image.WaterFilter();

        water.setAmplitude(amplitude);
        water.setAntialias(antialias);
        water.setPhase(phase);
        water.setWavelength(wavelength);


        ImageDeformation backDef = new ImageDeformationByFilters(
                new ImageFilter[]{});
        ImageDeformation textDef = new ImageDeformationByFilters(
                new ImageFilter[]{});
        ImageDeformation postDef = new ImageDeformationByFilters(
                new ImageFilter[]{water});

        //word generator
        com.octo.captcha.component.word.wordgenerator.WordGenerator dictionnaryWords = new com.octo.captcha.component.word.wordgenerator.ComposeDictionaryWordGenerator(
                new com.octo.captcha.component.word.FileDictionary(
                        "toddlist"));
        //wordtoimage components
        TextPaster randomPaster = new DecoratedRandomTextPaster(new Integer(6), new Integer(7), 
                new SingleColorGenerator(Color.black)
                , new TextDecorator[]{new BaffleTextDecorator(new Integer(numberOfHoles), Color.white)});
        BackgroundGenerator back = new UniColorBackgroundGenerator(
                new Integer(width), new Integer(height), Color.white);

        FontGenerator shearedFont = new RandomFontGenerator(new Integer(fontMinHeight),
                new Integer(fontMaxHeight));
        //word2image 1
        com.octo.captcha.component.image.wordtoimage.WordToImage word2image;
        word2image = new DeformedComposedWordToImage(shearedFont, back, randomPaster,
                backDef,
                textDef,
                postDef
        );

        return new com.octo.captcha.image.gimpy.GimpyFactory(dictionnaryWords,
                        word2image);

    }
    protected void rebuildFactories(  ) {

    	GimpyFactory [] factories = {
    			createFactory(),	
    	};
    	this.setFactories( factories );
    }

	@Override
	public void compose(ComponentManager manager) throws ComponentException {
		
	}

	@Override
	public void configure(Element root, Server server)
			throws ComponentException {

		amplitude = Double.parseDouble( root.getChildText("amplitude") );
		antialias = Boolean.parseBoolean(root.getChildText("antialias") );
		phase = Double.parseDouble( root.getChildText("phase") );
		wavelength = Double.parseDouble( root.getChildText("wavelength") );
		textColor = Color.black; // TODO: configure color by name
		bgColor = Color.white; // TODO: configure color by name
		width = Integer.parseInt( root.getChildText("width") );
		height = Integer.parseInt( root.getChildText("height") );
		fontMinHeight = Integer.parseInt( root.getChildText("fontMinHeight") );
		fontMaxHeight = Integer.parseInt( root.getChildText("fontMaxHeight") );
		numberOfHoles = Integer.parseInt( root.getChildText("numberOfHoles") );
		
	}

	@Override
	public void initialize() throws Exception {
		// TODO Auto-generated method stub
		
		rebuildFactories();
		
	}

}
